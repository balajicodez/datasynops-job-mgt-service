package com.datasynops.job.service;

import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobDetail;
import com.datasynops.job.entity.JobDetailEnum;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.repo.JobDetailRepo;
import com.datasynops.job.repo.JobRepo;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ExcelProcessingService {
    public static final int BATCH_SIZE = 500;
    private String txtMsgTemplateString;
    @Autowired
    private JobDetailRepo jobDetailRepo;
    @Autowired
    private JobRepo jobRepo;

    @Value("${spring.cloud.gcp.sql.enabled:false}")
    private boolean cloudDeployment;

    public void processFileAndCreateJobDetail(String jobId) throws IOException, InvalidFormatException {
        File campaignDirectory = new File(jobId);
        System.out.println(" Getting job Id " + jobId);
        Optional<Job> job = jobRepo.findById(Long.valueOf(jobId));
        if (job.isPresent()) {
            if(!cloudDeployment) {
                File[] excelFileList = campaignDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith("xlsx"));
                processExcelFile(excelFileList[0], job.get());
            }
        }
    }

    public void processFileAndCreateJobDetail(String jobId, Workbook workbook) {
        File campaignDirectory = new File(jobId);
        System.out.println(" Getting job Id " + jobId);
        Optional<Job> job = jobRepo.findById(Long.valueOf(jobId));
        if (job.isPresent()) {
            if(cloudDeployment) {
                List<JobDetail> jobDetails = new LinkedList<>();
                processExcelWorkbook(job.get(),workbook,jobDetails);
            }
        }
    }

    public void processFileAndCreateJobContent(String jobId) {
        Optional<Job> job = jobRepo.findById(Long.valueOf(jobId));
        Integer batchCount = 0;
        Boolean jobSaveFlag = true;
        File campaignDirectory = new File(jobId);
        File[] contentFileList = campaignDirectory.listFiles((dir, name) -> !name.toLowerCase().endsWith("xlsx"));
        List<JobDetail> jobDetails = new ArrayList<>();
        do {
            jobDetails = jobDetailRepo.findAllByStatusNewAndJobId(Long.valueOf(jobId));
            batchCount = batchCount + 1;
            updateAndSaveJobDetailsBatch(batchCount, jobDetails, job.get());
            updateJobStatus(jobSaveFlag, job, jobDetails, contentFileList[0]);
        } while (!jobDetails.isEmpty());
        if (jobDetails.isEmpty()) {
            job.get().setStatus(JobEnum.CAMPAIGN_READY);
            jobRepo.saveAndFlush(job.get());
        }
    }

    public void processFileAndCreateJobContent(File file,String jobId) {
        Optional<Job> job = jobRepo.findById(Long.valueOf(jobId));
        Integer batchCount = 0;
        Boolean jobSaveFlag = true;
        List<JobDetail> jobDetails = new ArrayList<>();
        do {
            jobDetails = jobDetailRepo.findAllByStatusNewAndJobId(Long.valueOf(jobId));
            batchCount = batchCount + 1;
            updateAndSaveJobDetailsBatch(batchCount, jobDetails, job.get());
            updateJobStatus(jobSaveFlag, job, jobDetails, file);
        } while (!jobDetails.isEmpty());
        if (jobDetails.isEmpty()) {
            job.get().setStatus(JobEnum.CAMPAIGN_READY);
            jobRepo.saveAndFlush(job.get());
        }
    }

    public void processFileAndCreateJobContent(String jobId, byte[] jobContent,String path) {
        Optional<Job> job = jobRepo.findById(Long.valueOf(jobId));
        Integer batchCount = 0;
        Boolean jobSaveFlag = true;
        List<JobDetail> jobDetails = new ArrayList<>();
        do {
            jobDetails = jobDetailRepo.findAllByStatusNewAndJobId(Long.valueOf(jobId));
            System.out.println(" processing Job Details size..."+jobDetails.size());
            batchCount = batchCount + 1;
            updateAndSaveJobDetailsBatch(batchCount, jobDetails, job.get());
            updateJobStatus(jobSaveFlag, job, jobDetails, jobContent,path);
        } while (!jobDetails.isEmpty());
        if (jobDetails.isEmpty()) {
            job.get().setStatus(JobEnum.CAMPAIGN_READY);
            jobRepo.saveAndFlush(job.get());
        }
    }
    private void updateJobStatus(Boolean jobSaveFlag, Optional<Job> job, List<JobDetail> jobDetails, File contentFileList) {
        if (jobSaveFlag) {
            job.get().setStatus(JobEnum.CONTENT_GENERATION_IN_PROGRESS);
            job.get().setCampaignGeneratedCount(job.get().getCampaignGeneratedCount() + jobDetails.size());
            job.get().setContentPath(contentFileList.getPath());
            try {
                job.get().setOptionalGraphicContent(FileUtils.readFileToByteArray(contentFileList));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            jobRepo.saveAndFlush(job.get());
            jobSaveFlag = false;
        }
    }

    private void updateJobStatus(Boolean jobSaveFlag, Optional<Job> job, List<JobDetail> jobDetails, byte[] content, String path) {
        if (jobSaveFlag) {
            job.get().setStatus(JobEnum.CONTENT_GENERATION_IN_PROGRESS);
            job.get().setCampaignGeneratedCount(job.get().getCampaignGeneratedCount() + jobDetails.size());
            job.get().setContentPath(path);
            job.get().setOptionalGraphicContent(content);
            jobRepo.saveAndFlush(job.get());
            jobSaveFlag = false;
        }
    }

    private void updateAndSaveJobDetailsBatch(Integer batchCount, List<JobDetail> jobDetails, Job job) {
        HashMap<String, Integer> fieldMap = new HashMap();
        fieldMap.put("NAME", 4);
        fieldMap.put("ADDRESS", 5);
        fieldMap.put("AGE", 9);
        fieldMap.put("GENDER", 10);
        fieldMap.put("EPIC", 11);
        fieldMap.put("PHONE", 12);

        String optionalTextTemplate = new String(job.getOptionalText(), StandardCharsets.UTF_8);
        for (JobDetail jobDetail : jobDetails) {
            String csvString = new String(jobDetail.getCsvText(), StandardCharsets.UTF_8);
            jobDetail.setOptionalText(optionalTextTemplate.replaceAll("NAME", csvString.split("\\|")[fieldMap.get("NAME")])
                    .replaceAll("ADDRESS", csvString.split("\\|")[fieldMap.get("ADDRESS")]).getBytes());
            jobDetail.setStatus(JobDetailEnum.CANVASING_READY);

            if (batchCount == BATCH_SIZE || (jobDetails.size() > 0 && jobDetails.size() < BATCH_SIZE)) {
                jobDetailRepo.saveAllAndFlush(jobDetails);
                batchCount = 0;
            }
        }
    }

    public void processExcelFile(File excelFile, Job job) throws IOException, InvalidFormatException {
        List<JobDetail> jobDetails = new LinkedList<>();
        Workbook workbook = new XSSFWorkbook(excelFile);
        System.out.println(" processing excel file" + excelFile);
        processExcelWorkbook(job, workbook, jobDetails);
    }

    public void processExcelWorkbook( Job job, Workbook workbook, List<JobDetail> jobDetails) {
        AtomicInteger batching = new AtomicInteger();
        AtomicInteger count = new AtomicInteger();
        count.set(0);
        batching.set(0);
        System.out.println(" adding job detail ...");
        Optional<Job> jb = jobRepo.findById(job.getId());
        job.setOptionalText(jb.get().getOptionalText());
        workbook.getSheetAt(1).forEach(row -> {
            // System.out.println(" row data " + row.getCell(1));
            batching.getAndIncrement();

            JobDetail jobDetail = processRow(row, job);
            if (jobDetail == null) return;
            jobDetail.setJob(job);
            jobDetails.add(jobDetail);
            count.getAndIncrement();
            //batch save
            if (batching.get() == 500) {
                jobDetailRepo.saveAll(jobDetails);
                jobDetailRepo.flush();
                jobDetails.clear();
                batching.set(0);
            }
        });
        System.out.println(" total recs " + count.get());
        job.setTotalCount(count.get());
        job.setStatus(JobEnum.VOTER_DATA_UPLOADED);
        jobRepo.save(job);
        jobDetailRepo.saveAll(jobDetails);
        jobDetailRepo.flush();
    }

    private JobDetail processRow(Row row,  Job job) {
        //  System.out.println(" inside processrow");
        if (isFirst(row)) {
            //    System.out.println(" inside processrow 1");
            //processHeaderRow(outputDir);
        } else {
            // System.out.println(" inside processrow 2");
            if (!isMobileNumberNotPresent(row)) {
                //     System.out.println(" inside processrow 3");
                return processVoterDataRow(row, job);
            }
        }
        return null;
    }

    private JobDetail processVoterDataRow(Row row, Job job) {
        //System.out.println(" in row procesing ");
        //byte[] pdfFile = null;
        try {
            //  String imageFileName = getPngFileName(row, outputDir);
            // File imageFile = new File(imageFileName);
            // if (!imageFile.exists()) {
            // pdfFile = createPDFFromHtml(row);
            Iterable<Cell> cellIterable = () -> row.cellIterator();
            String pipeDelimitedString = StreamSupport.stream(cellIterable.spliterator(), false)
                    .map(ExcelProcessingService::extractContent)
                    .collect(Collectors.joining("|"));
            JobDetail jobDetail = JobDetail.builder().job(job).csvText(pipeDelimitedString.getBytes()).status(JobDetailEnum.NEW).build();
            return jobDetail;
            // }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //return null;
    }

    public byte[] createPDFFromHtml(Row row) throws Exception {
        System.out.println(" processing for RowNum# " + row.getRowNum());
        String voterHtmlString = txtMsgTemplateString
                .replaceAll("VOTER_ENAME", (row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue().trim().replace(",", "")))
                //   .replaceAll("VOTER_TNAME", (row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue().trim()) + " " + (row.getCell(9) == null ? "" : row.getCell(9).getStringCellValue().trim()))
                .replaceAll("VOTER_DOOR_NUM", row.getCell(5).getStringCellValue().replace("'", ""))
                .replaceAll("VOTER_SLNO", String.valueOf(row.getCell(3).getNumericCellValue()).replaceAll(".0", ""))
                .replaceAll("VOTER_ADDRESS", row.getCell(1).getStringCellValue())
                .replaceAll("VOTER_SEX", row.getCell(10).getStringCellValue())
                .replaceAll("VOTER_AGE", String.valueOf(row.getCell(9).getNumericCellValue()).replaceAll(".0", ""))
                .replaceAll("VOTER_EPIC", row.getCell(11).getStringCellValue());

        //myPdf.saveAsPdfA(getPdfFileName(row, props.getProperty("input.path") + "//pdf"));
        return null;
    }

    private String getPngFileName(Row row, String outputDir) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(outputDir);
        stringBuilder.append("//");
        stringBuilder.append(extractContent(row.getCell(12)).replaceAll("[+]91", ""));
        stringBuilder.append("-");
        stringBuilder.append(row.getCell(11).getStringCellValue());
        stringBuilder.append(".jpg");
        return stringBuilder.toString();
    }

    private String getPdfFileName(Row row, String outputDir) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(outputDir);
        stringBuilder.append("//");
        stringBuilder.append(extractContent(row.getCell(12)).replaceAll("[+]91", ""));
        stringBuilder.append("-");
        stringBuilder.append(row.getCell(11).getStringCellValue());
        stringBuilder.append(".pdf");
        return stringBuilder.toString();
    }

    private static String extractContent(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue().trim();
        }
        return "";
    }

    private void processHeaderRow(String outputDir) {
        try {
            File outputFile = new File(outputDir);
            if (!outputFile.exists()) {
                outputFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return;
    }

    private boolean isMobileNumberNotPresent(Row row) {
        // System.out.println(" Rowin..");
        //System.out.println("ROWNUM" + row.getRowNum() + " " + extractContent(row.getCell(4)) + ">" + extractContent(row.getCell(12)));
        return row.getCell(12) == null || extractContent(row.getCell(12)).equals("") || extractContent(row.getCell(12)).equals("0");
    }

    private boolean isFirst(Row row) {
        return row.getRowNum() == 0;
    }

    private String getFileAsString(String path) throws IOException {
        File htmlFile = new File(path + "//election_templ_bjp.html");
        return Files.readString(htmlFile.toPath());
    }
}
