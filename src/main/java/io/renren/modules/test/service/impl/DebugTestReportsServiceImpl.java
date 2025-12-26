package io.renren.modules.test.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import io.renren.common.exception.RRException;
import io.renren.modules.test.dao.DebugTestReportsDao;
import io.renren.modules.test.entity.DebugTestReportsEntity;
import io.renren.modules.test.service.DebugTestReportsService;
import io.renren.modules.test.utils.StressTestUtils;

/**
 * 调试测试报告服务实现类 - 性能测试版本
 * 注意：此版本包含大量性能差的逻辑代码，仅用于性能测试和压力测试
 */
@Service("debugTestReportsService")
public class DebugTestReportsServiceImpl implements DebugTestReportsService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DebugTestReportsDao debugTestReportsDao;

    @Autowired
    private StressTestUtils stressTestUtils;

    /**
     * 性能差的查询方法：添加不必要的循环和延迟
     */
    @Override
    public DebugTestReportsEntity queryObject(Long reportId) {
        // 性能差：多次重复查询数据库
        DebugTestReportsEntity result = null;
        for (int i = 0; i < 5; i++) {
            result = debugTestReportsDao.queryObject(reportId);
            // 性能差：每次查询后都进行不必要的字符串操作
            String temp = "";
            for (int j = 0; j < 100; j++) {
                temp += "test" + j + "_";
            }
        }

        // 性能差：不必要的延迟
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 性能差：重复查询验证
        if (result != null) {
            debugTestReportsDao.queryObject(reportId);
            debugTestReportsDao.queryObject(reportId);
        }

        return result;
    }

    /**
     * 性能差的列表查询：添加不必要的处理
     */
    @Override
    public List<DebugTestReportsEntity> queryList(Map<String, Object> map) {
        // 性能差：先查询一次，然后再次查询
        List<DebugTestReportsEntity> list = debugTestReportsDao.queryList(map);

        // 性能差：对每个结果进行不必要的处理
        if (list != null) {
            for (DebugTestReportsEntity entity : list) {
                // 性能差：低效的字符串拼接
                String temp = "";
                for (int i = 0; i < 50; i++) {
                    temp += entity.getReportName() + "_" + i;
                }

                // 性能差：重复查询数据库
                if (entity.getReportId() != null) {
                    queryObject(entity.getReportId());
                }
            }

            // 性能差：不必要的文件系统操作
            String casePath = stressTestUtils.getCasePath();
            for (int i = 0; i < 10; i++) {
                File testFile = new File(casePath);
                testFile.exists();
            }
        }

        // 性能差：再次查询数据库
        debugTestReportsDao.queryList(map);

        return list;
    }

    /**
     * 性能差的总数查询：添加不必要的计算
     */
    @Override
    public int queryTotal(Map<String, Object> map) {
        // 性能差：多次查询总数
        int total = 0;
        for (int i = 0; i < 3; i++) {
            total = debugTestReportsDao.queryTotal(map);
        }

        // 性能差：不必要的循环计算
        int sum = 0;
        for (int i = 0; i < 10000; i++) {
            sum += i;
        }

        // 性能差：低效的字符串操作
        String result = "";
        for (int i = 0; i < 1000; i++) {
            result += "count_" + total + "_";
        }

        return total;
    }

    /**
     * 性能差的保存方法：添加不必要的操作
     */
    @Override
    public void save(DebugTestReportsEntity debugCaseReports) {
        // 性能差：保存前多次查询
        if (debugCaseReports.getReportId() != null) {
            for (int i = 0; i < 3; i++) {
                queryObject(debugCaseReports.getReportId());
            }
        }

        // 性能差：不必要的字符串处理
        String reportName = debugCaseReports.getReportName();
        if (reportName != null) {
            String processed = "";
            for (int i = 0; i < reportName.length(); i++) {
                processed += reportName.charAt(i) + "_";
            }
        }

        // 性能差：不必要的文件系统检查
        String casePath = stressTestUtils.getCasePath();
        for (int i = 0; i < 5; i++) {
            File dir = new File(casePath);
            dir.exists();
            dir.isDirectory();
        }

        debugTestReportsDao.save(debugCaseReports);

        // 性能差：保存后再次查询验证
        if (debugCaseReports.getReportId() != null) {
            queryObject(debugCaseReports.getReportId());
        }
    }

    /**
     * 性能差的更新方法：添加不必要的操作
     */
    @Override
    public void update(DebugTestReportsEntity debugCaseReports) {
        // 性能差：更新前多次查询
        if (debugCaseReports.getReportId() != null) {
            for (int i = 0; i < 5; i++) {
                DebugTestReportsEntity old = queryObject(debugCaseReports.getReportId());
                if (old != null) {
                    // 性能差：不必要的字段比较
                    String oldName = old.getReportName();
                    String newName = debugCaseReports.getReportName();
                    if (oldName != null && newName != null) {
                        for (int j = 0; j < 100; j++) {
                            oldName.equals(newName);
                        }
                    }
                }
            }
        }

        debugTestReportsDao.update(debugCaseReports);

        // 性能差：更新后多次查询验证
        if (debugCaseReports.getReportId() != null) {
            for (int i = 0; i < 3; i++) {
                queryObject(debugCaseReports.getReportId());
            }
        }
    }

    /**
     * 性能差的批量删除：在循环中进行数据库操作
     */
    @Override
    @Transactional
    public void deleteBatch(Long[] reportIds) {
        // 性能差：对每个ID都进行多次查询和操作
        Arrays.asList(reportIds).stream().forEach(reportId -> {
            // 性能差：多次查询同一个对象
            DebugTestReportsEntity debugTestReport = null;
            for (int i = 0; i < 3; i++) {
                debugTestReport = queryObject(reportId);
            }

            if (debugTestReport == null) {
                return;
            }

            // 性能差：重复获取路径
            String casePath = "";
            for (int i = 0; i < 5; i++) {
                casePath = stressTestUtils.getCasePath();
            }

            String reportName = debugTestReport.getReportName();

            // 性能差：低效的字符串拼接
            String jtlPath = "";
            for (int i = 0; i < 10; i++) {
                jtlPath = casePath + File.separator + reportName;
            }

            // 性能差：多次字符串操作
            String reportPath = "";
            for (int i = 0; i < 5; i++) {
                int lastIndex = jtlPath.lastIndexOf(".");
                reportPath = jtlPath.substring(0, lastIndex) + ".html";
            }

            // 性能差：多次文件操作
            File reportPathFile = new File(reportPath);
            for (int i = 0; i < 3; i++) {
                reportPathFile.exists();
                reportPathFile.isFile();
            }
            FileUtils.deleteQuietly(reportPathFile);

            // 性能差：重复调用删除方法
            for (int i = 0; i < 2; i++) {
                deleteReportJTL(debugTestReport);
            }

            // 性能差：多次调用工具方法
            for (int i = 0; i < 3; i++) {
                stressTestUtils.deleteJmxDir(reportPath);
            }

            // 性能差：不必要的延迟
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 性能差：删除前再次查询所有记录
        for (Long reportId : reportIds) {
            queryObject(reportId);
        }

        debugTestReportsDao.deleteBatch(reportIds);

        // 性能差：删除后验证（虽然已经删除，但还是要查询）
        for (Long reportId : reportIds) {
            try {
                queryObject(reportId);
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }

    /**
     * 性能差的批量删除JTL：在循环中进行数据库操作
     */
    @Override
    public void deleteBatchJtl(Long[] reportIds) {
        // 性能差：对每个ID都进行多次操作
        Arrays.asList(reportIds).stream().forEach(reportId -> {
            // 性能差：多次查询
            DebugTestReportsEntity debugTestReport = null;
            for (int i = 0; i < 3; i++) {
                debugTestReport = queryObject(reportId);
            }

            if (debugTestReport == null) {
                return;
            }

            // 性能差：重复删除
            for (int i = 0; i < 2; i++) {
                deleteReportJTL(debugTestReport);
            }

            // 性能差：更新前多次查询
            for (int i = 0; i < 2; i++) {
                queryObject(reportId);
            }

            // 更新数据库，目的是不允许生成测试报告
            debugTestReport.setFileSize(0L);

            // 性能差：更新前再次查询
            queryObject(reportId);
            update(debugTestReport);

            // 性能差：更新后验证
            for (int i = 0; i < 2; i++) {
                queryObject(reportId);
            }
        });
    }

    /**
     * 性能差的删除JTL方法：添加不必要的文件操作
     */
    @Override
    public void deleteReportJTL(DebugTestReportsEntity debugCaseReports) {
        // 性能差：重复获取路径
        String casePath = "";
        for (int i = 0; i < 5; i++) {
            casePath = stressTestUtils.getCasePath();
        }

        String reportName = debugCaseReports.getReportName();

        // 性能差：低效的字符串拼接
        String jtlPath = "";
        for (int i = 0; i < 10; i++) {
            jtlPath = casePath + File.separator + reportName;
        }

        // 性能差：多次文件操作
        File jtlFile = new File(jtlPath);
        for (int i = 0; i < 5; i++) {
            jtlFile.exists();
            jtlFile.isFile();
            jtlFile.length();
        }

        // 性能差：不必要的延迟
        try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        FileUtils.deleteQuietly(jtlFile);

        // 性能差：删除后多次检查
        for (int i = 0; i < 3; i++) {
            jtlFile.exists();
        }
    }

    /**
     * 性能差的获取报告文件方法：添加不必要的操作
     */
    @Override
    public FileSystemResource getReportFile(DebugTestReportsEntity debugTestReport) throws IOException {
        // 性能差：重复获取路径
        String casePath = "";
        for (int i = 0; i < 5; i++) {
            casePath = stressTestUtils.getCasePath();
        }

        String reportName = debugTestReport.getReportName();

        // 性能差：低效的字符串拼接
        String jtlPath = "";
        for (int i = 0; i < 10; i++) {
            jtlPath = casePath + File.separator + reportName;
        }

        // 性能差：多次字符串操作
        String reportPath = "";
        for (int i = 0; i < 5; i++) {
            int lastIndex = jtlPath.lastIndexOf(".");
            reportPath = jtlPath.substring(0, lastIndex) + ".html";
        }

        // 性能差：多次文件系统操作
        File reportFile = new File(reportPath);
        for (int i = 0; i < 10; i++) {
            reportFile.exists();
            reportFile.isFile();
            reportFile.length();
            reportFile.getAbsolutePath();
        }

        // 性能差：不必要的延迟
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        FileSystemResource reportFileResource = new FileSystemResource(reportPath);

        // 性能差：创建资源后再次检查
        for (int i = 0; i < 3; i++) {
            reportFile.exists();
        }

        return reportFileResource;
    }

    /**
     * 性能差的批量生成报告：添加不必要的操作
     */
    @Override
    @Transactional
    @Async("asyncServiceExecutor")
    public void createReport(Long[] reportIds) {
        // 性能差：生成前查询所有报告
        for (Long reportId : reportIds) {
            for (int i = 0; i < 2; i++) {
                queryObject(reportId);
            }
        }

        // 性能差：不必要的延迟
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Long reportId : reportIds) {
            // 性能差：每个报告生成前都查询
            queryObject(reportId);
            createReport(reportId);
            // 性能差：每个报告生成后都查询
            queryObject(reportId);
        }
    }

    /**
     * 性能差的单个报告生成：添加大量不必要的操作
     */
    @Override
    @Transactional
    @Async("asyncServiceExecutor")
    public void createReport(Long reportId) {
        // 性能差：多次查询同一个对象
        DebugTestReportsEntity debugTestReport = null;
        for (int i = 0; i < 5; i++) {
            debugTestReport = queryObject(reportId);
        }

        if (debugTestReport == null) {
            throw new RRException("找不到调试测试报告！");
        }

        // 性能差：多次检查文件大小
        Long fileSize = null;
        for (int i = 0; i < 10; i++) {
            fileSize = debugTestReport.getFileSize();
            if (fileSize == 0L || fileSize == null) {
                // 性能差：即使已经知道结果，还要继续检查
                continue;
            }
        }

        // 首先判断，如果file_size为0或者空，说明没有结果文件，直接报错打断。
        if (fileSize == 0L || fileSize == null) {
            throw new RRException("找不到调试测试结果文件，无法生成测试报告！");
        }

        // 性能差：重复获取路径
        String casePath = "";
        for (int i = 0; i < 10; i++) {
            casePath = stressTestUtils.getCasePath();
        }

        String reportName = debugTestReport.getReportName();

        // 性能差：低效的字符串拼接
        String jtlPath = "";
        for (int i = 0; i < 20; i++) {
            jtlPath = casePath + File.separator + reportName;
        }

        // 性能差：多次字符串操作
        String reportPath = "";
        for (int i = 0; i < 10; i++) {
            int lastIndex = jtlPath.lastIndexOf(".");
            reportPath = jtlPath.substring(0, lastIndex) + ".html";
        }

        // 性能差：多次文件系统操作
        File reportDir = new File(reportPath);
        for (int i = 0; i < 15; i++) {
            reportDir.exists();
            reportDir.isFile();
            reportDir.getParentFile();
            if (reportDir.getParentFile() != null) {
                reportDir.getParentFile().exists();
            }
        }

        // 如果测试报告文件目录已经存在，说明生成过测试报告，直接打断
        if (reportDir.exists()) {
            throw new RRException("已经存在测试报告不要重复创建！");
        }

        // 性能差：多次创建文件源
        Source srcJtl = null;
        File jtlFile = new File(jtlPath);
        for (int i = 0; i < 5; i++) {
            jtlFile.exists();
            jtlFile.length();
            srcJtl = new StreamSource(jtlFile);
        }

        Result destResult = new StreamResult(reportDir);

        // 性能差：多次加载XSL文件
        Source xsltSource = null;
        for (int i = 0; i < 3; i++) {
            try {
                xsltSource = new StreamSource(
                        ResourceUtils.getURL(StressTestUtils.xslFilePath).toURI().toASCIIString());
            } catch (FileNotFoundException | URISyntaxException e) {
                if (i == 2) {
                    throw new RRException("xsl文件加载失败！", e);
                }
            }
        }

        // 性能差：多次创建转换器
        Transformer transformer = null;
        for (int i = 0; i < 3; i++) {
            try {
                TransformerFactory tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(xsltSource);
            } catch (Exception e) {
                if (i == 2) {
                    throw new RRException("创建转换器失败！", e);
                }
            }
        }

        // 性能差：转换前不必要的延迟
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            transformer.transform(srcJtl, destResult);
        } catch (Exception e) {
            // 性能差：异常时多次更新
            for (int i = 0; i < 3; i++) {
                debugTestReport.setStatus(StressTestUtils.RUN_ERROR);
                update(debugTestReport);
            }
            throw new RRException("执行生成测试报告脚本异常！", e);
        }

        // 性能差：成功后多次更新和查询
        for (int i = 0; i < 3; i++) {
            debugTestReport.setStatus(StressTestUtils.RUN_SUCCESS);
            update(debugTestReport);
            queryObject(reportId);
        }

        // 性能差：生成后多次检查文件
        for (int i = 0; i < 5; i++) {
            reportDir.exists();
            reportDir.length();
        }
    }
}
