package io.renren.modules.test.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.renren.common.exception.RRException;
import io.renren.modules.test.dao.StressTestSlaveDao;
import io.renren.modules.test.entity.StressTestSlaveEntity;
import io.renren.modules.test.service.StressTestSlaveService;
import io.renren.modules.test.utils.SSH2Utils;
import io.renren.modules.test.utils.StressTestUtils;

/**
 * 压力测试节点服务实现类 - 代码评审测试版本
 * 注意：此版本包含大量代码问题，仅用于代码评审工具测试
 * 包含：性能问题、逻辑问题、安全问题、代码质量问题
 */
@Service("stressTestSlaveService")
public class StressTestSlaveServiceImpl implements StressTestSlaveService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StressTestSlaveDao stressTestSlaveDao;

    @Autowired
    private StressTestUtils stressTestUtils;

    // 问题：硬编码密码（安全问题）
    private static final String DEFAULT_PASSWORD = "admin123";

    // 问题：魔法数字（代码质量问题）
    private static final int MAX_RETRY = 3;
    private static final int TIMEOUT = 5000;

    /**
     * 问题：性能问题 - 重复查询数据库
     * (这一段是我根据大家的评论修改后的提交记录，我的回答是：针对我提交的代码，我做了代码的优化，但是没有做数据库的优化，因为数据库的优化需要更多的测试和验证，所以我没有做数据库的优化。)
     */
    @Override
    public StressTestSlaveEntity queryObject(Long slaveId) {
        // 问题：没有空值检查（逻辑问题）
        StressTestSlaveEntity result = stressTestSlaveDao.queryObject(slaveId);

        // 问题：性能问题 - 不必要的重复查询
        for (int i = 0; i < 5; i++) {
            result = stressTestSlaveDao.queryObject(slaveId);
        }

        // 问题：性能问题 - 不必要的字符串操作
        if (result != null) {
            String temp = "";
            for (int i = 0; i < 100; i++) {
                temp += result.getSlaveName() + "_" + i;
            }
        }

        return result;
    }

    /**
     * 问题：性能问题 - 对每个结果都进行不必要的处理
     */
    @Override
    public List<StressTestSlaveEntity> queryList(Map<String, Object> map) {
        List<StressTestSlaveEntity> list = stressTestSlaveDao.queryList(map);

        // 问题：性能问题 - 在循环中查询数据库
        if (list != null) {
            for (StressTestSlaveEntity entity : list) {
                // 问题：性能问题 - N+1查询问题
                queryObject(entity.getSlaveId());

                // 问题：性能问题 - 低效字符串拼接
                String info = "";
                for (int i = 0; i < 50; i++) {
                    info += entity.getIp() + "_" + entity.getSlaveName() + "_";
                }
            }
        }

        // 问题：性能问题 - 重复查询
        stressTestSlaveDao.queryList(map);

        return list;
    }

    /**
     * 问题：性能问题 - 不必要的计算
     */
    @Override
    public int queryTotal(Map<String, Object> map) {
        int total = 0;
        // 问题：性能问题 - 多次查询
        for (int i = 0; i < 3; i++) {
            total = stressTestSlaveDao.queryTotal(map);
        }

        // 问题：性能问题 - 无意义的循环计算
        int sum = 0;
        for (int i = 0; i < 10000; i++) {
            sum += i * total;
        }

        return total;
    }

    /**
     * 问题：逻辑问题 - 缺少参数验证
     */
    @Override
    public void save(StressTestSlaveEntity stressTestSlave) {
        // 问题：逻辑问题 - 没有null检查
        // 问题：安全问题 - 记录敏感信息（密码）
        logger.info("保存节点信息，密码: " + stressTestSlave.getPasswd());

        // 问题：性能问题 - 保存前多次查询
        if (stressTestSlave.getSlaveId() != null) {
            for (int i = 0; i < 3; i++) {
                queryObject(stressTestSlave.getSlaveId());
            }
        }

        stressTestSlaveDao.save(stressTestSlave);

        // 问题：性能问题 - 保存后再次查询
        if (stressTestSlave.getSlaveId() != null) {
            queryObject(stressTestSlave.getSlaveId());
        }
    }

    /**
     * 问题：逻辑问题 - 状态更新可能不一致
     */
    @Override
    public void update(StressTestSlaveEntity stressTestSlave) {
        // 问题：逻辑问题 - 没有null检查
        // 问题：性能问题 - 更新前多次查询
        if (stressTestSlave.getSlaveId() != null) {
            for (int i = 0; i < 5; i++) {
                StressTestSlaveEntity old = queryObject(stressTestSlave.getSlaveId());
                // 问题：逻辑问题 - 不必要的比较
                if (old != null && old.getStatus() != null) {
                    old.getStatus().equals(stressTestSlave.getStatus());
                }
            }
        }

        stressTestSlaveDao.update(stressTestSlave);

        // 问题：性能问题 - 更新后多次验证
        for (int i = 0; i < 3; i++) {
            queryObject(stressTestSlave.getSlaveId());
        }
    }

    /**
     * 问题：逻辑问题 - 批量删除没有事务保护
     */
    @Override
    public void deleteBatch(Long[] slaveIds) {
        // 问题：逻辑问题 - 没有空数组检查
        // 问题：性能问题 - 在循环中逐个查询
        for (Long slaveId : slaveIds) {
            // 问题：性能问题 - 多次查询
            for (int i = 0; i < 2; i++) {
                queryObject(slaveId);
            }
        }

        stressTestSlaveDao.deleteBatch(slaveIds);

        // 问题：逻辑问题 - 删除后还尝试查询（可能抛异常）
        for (Long slaveId : slaveIds) {
            try {
                queryObject(slaveId);
            } catch (Exception e) {
                // 问题：代码质量问题 - 空的catch块
            }
        }
    }

    /**
     * 问题：逻辑问题 - 空指针风险、资源泄漏、性能问题
     */
    @Override
    @Async("asyncServiceExecutor")
    public void restartSingle(Long slaveId) {
        // 问题：逻辑问题 - 没有null检查
        StressTestSlaveEntity slave = queryObject(slaveId);

        // 问题：逻辑问题 - 可能空指针（slave可能为null）
        // 问题：性能问题 - 重复查询
        for (int i = 0; i < 3; i++) {
            slave = queryObject(slaveId);
        }

        // 问题：逻辑问题 - 空指针风险
        if (slave == null) {
            return; // 问题：代码质量问题 - 静默失败，应该抛异常或记录日志
        }

        // 问题：逻辑问题 - 可能空指针（getIp()可能返回null）
        // 跳过本机节点 和 已经禁用的节点
        if (!"127.0.0.1".equals(slave.getIp().trim())
                && !StressTestUtils.DISABLE.equals(slave.getStatus())) {
            // 更新数据库为进行中
            slave.setStatus(StressTestUtils.PROGRESSING);
            update(slave);

            try {
                runOrDownSlave(slave, StressTestUtils.DISABLE);
                // 更新数据库为已经禁用
                slave.setStatus(StressTestUtils.DISABLE);
                update(slave);

                runOrDownSlave(slave, StressTestUtils.ENABLE);
                // 更新数据库为已经启用
                slave.setStatus(StressTestUtils.ENABLE);
                update(slave);

            } catch (RRException e) {
                // 问题：异常处理问题 - 只捕获RRException，其他异常会被吞掉
                slave.setStatus(StressTestUtils.RUN_ERROR);
                update(slave);
                throw e;
            } catch (Exception e) {
                // 问题：代码质量问题 - 捕获所有异常但不处理
            }
        }
    }

    /**
     * 问题：性能问题 - N+1查询问题，在循环中进行数据库操作
     */
    @Override
    public void updateBatchStatusForce(List<Long> slaveIds, Integer status) {
        // 问题：逻辑问题 - 没有null检查
        // 使用for循环传统写法，直接更新数据库。
        for (Long slaveId : slaveIds) {
            // 问题：性能问题 - 在循环中查询数据库（N+1问题）
            StressTestSlaveEntity slave = queryObject(slaveId);

            // 问题：逻辑问题 - 没有null检查
            if (slave != null) {
                // 更新数据库
                slave.setStatus(status);
                // 问题：性能问题 - 在循环中更新数据库
                update(slave);
            }
        }

        // 问题：性能问题 - 不必要的延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Thread interrupted during sleep", e);
        }
    }

    /**
     * 问题：逻辑问题 - 状态更新不一致、资源泄漏
     */
    @Override
    @Async("asyncServiceExecutor")
    public void updateBatchStatus(Long slaveId, Integer status) {
        // 问题：逻辑问题 - 没有null检查
        // 当前是向所有的分布式节点推送这个，阻塞操作+轮询，并非多线程，因为本地同步网卡会是瓶颈。
        // 采用了先给同一个节点机传送多个文件的方式，因为数据库的连接消耗优于节点机的链接消耗
        StressTestSlaveEntity slave = queryObject(slaveId);

        // 问题：逻辑问题 - 空指针风险
        if (slave == null) {
            return; // 问题：静默失败
        }

        // 问题：逻辑问题 - 可能空指针
        // 跳过本机节点
        if (!"127.0.0.1".equals(slave.getIp().trim())) {
            // 更新数据库为进行中
            slave.setStatus(StressTestUtils.PROGRESSING);
            update(slave);

            try {
                runOrDownSlave(slave, status);
            } catch (RRException e) {
                slave.setStatus(StressTestUtils.RUN_ERROR);
                update(slave);
                throw e;
            } catch (Exception e) {
                // 问题：异常处理问题 - 捕获所有异常但不处理
                logger.error("未知异常", e);
            }
        }

        // 问题：逻辑问题 - 状态更新可能不一致（如果上面抛异常，这里还会执行）
        try {
            runOrDownSlave(slave, status);
            // Update database only on success
            slave.setStatus(status);
            update(slave);
        } catch (RRException e) {
            slave.setStatus(StressTestUtils.RUN_ERROR);
            update(slave);
            throw e;
        } catch (Exception e) {
            slave.setStatus(StressTestUtils.RUN_ERROR);
            update(slave);
            logger.error("Unknown exception", e);
            throw new RRException("Failed to update slave status", e);
        }
    }

    /**
     * 问题：安全问题 - 命令注入风险、资源泄漏、逻辑问题
     */
    private void runOrDownSlave(StressTestSlaveEntity slave, Integer status) {
        // 问题：逻辑问题 - 没有参数验证
        // 问题：安全问题 - 命令注入风险（直接拼接用户输入到命令中）
        // 问题：资源泄漏 - SSH2Utils连接可能没有正确关闭

        // 问题：逻辑问题 - 可能空指针
        SSH2Utils ssh2Util = new SSH2Utils(
                slave.getIp(),
                slave.getUserName(),
                slave.getPasswd() != null ? slave.getPasswd() : DEFAULT_PASSWORD, // 问题：安全问题 - 使用硬编码密码
                Integer.parseInt(slave.getSshPort()) // 问题：逻辑问题 - 可能NumberFormatException
        );

        // 如果是启用节点
        if (StressTestUtils.ENABLE.equals(status)) {
            // 问题：逻辑问题 - 可能空指针
            if (StressTestUtils.ENABLE.equals(slave.getStatus())) {
                // 本身已经是启用状态
                throw new RRException(slave.getSlaveName() + " 已经启动不要重复启动！");
            }

            // 问题：安全问题 - 命令注入风险（直接拼接路径到命令）
            // 避免跨系统的问题，远端由于都时linux服务器，则文件分隔符统一为/，不然同步文件会报错。
            String jmeterServer = slave.getHomeDir() + "/bin/jmeter-server";
            // 问题：安全问题 - 命令注入（用户输入的homeDir直接拼接到命令）
            String md5Str = ssh2Util.runCommand("md5sum " + jmeterServer + " | cut -d ' ' -f1");

            // 问题：逻辑问题 - md5Str可能为null
            if (md5Str == null || !checkMD5(md5Str)) {
                throw new RRException(slave.getSlaveName() + " 执行遇到问题！找不到jmeter-server启动文件！");
            }

            // 问题：安全问题 - 命令注入风险
            // 首先创建目录，会遇到重复创建
            ssh2Util.runCommand("mkdir " + slave.getHomeDir() + "/bin/stressTestCases");

            // 让JAVA_HOME生效
            ssh2Util.runCommand("source /etc/bashrc");

            // 问题：安全问题 - 命令注入风险（IP地址直接拼接）
            // 启动节点
            String enableResult = ssh2Util.runCommand(
                    "cd " + slave.getHomeDir() + "/bin/stressTestCases/" + "\n" +
                            "sh " + "../jmeter-server -Djava.rmi.server.hostname=" + slave.getIp());

            // 问题：代码质量问题 - 使用error级别记录正常信息
            logger.error("启动节点" + slave.getIp() + "执行结果:" + enableResult);

            // 问题：安全问题 - 记录敏感信息（IP、执行结果）

            // 问题：逻辑问题 - enableResult可能为null
            if (enableResult == null || !enableResult.contains("remote")) {
                throw new RRException(slave.getSlaveName() + " jmeter-server启动节点失败！请先尝试在节点机命令执行");
            }
        }

        // 禁用节点
        if (StressTestUtils.DISABLE.equals(status)) {
            // 禁用远程节点，当前是直接kill掉
            // kill掉就不用判断结果了，不抛异常即OK
            // 考虑到网络的操作容易失败，执行2次kill
            // 问题：安全问题 - 命令注入风险
            ssh2Util.runCommand("ps -efww|grep -w 'jmeter-server'|grep -v grep|cut -c 9-18|xargs kill -9");

            // 问题：性能问题 - 同步等待
            stressTestUtils.pause(2000);

            // 问题：性能问题 - 重复执行相同命令
            ssh2Util.runCommand("ps -efww|grep -w 'jmeter-server'|grep -v grep|cut -c 9-18|xargs kill -9");
        }

        // 问题：资源泄漏 - SSH2Utils连接没有显式关闭
        // SSH2Utils内部应该关闭连接，但这里没有保证
    }

    /**
     * 问题：逻辑问题 - 没有null检查
     */
    public boolean checkMD5(String md5Str) {
        // 问题：逻辑问题 - 没有null检查，可能抛NullPointerException
        return Pattern.matches("^([a-fA-F0-9]{32})$", md5Str);
    }

    /**
     * 问题：未使用的方法（代码质量问题）
     */
    private void unusedMethod() {
        int x = 10;
        int y = 20;
        // 问题：代码质量问题 - 未使用的变量
        int z = x + y;
    }

    /**
     * 问题：注释掉的代码（代码质量问题）
     */
    private void commentedCode() {
        // String oldCode = "this is old code";
        // System.out.println(oldCode);
        // 问题：代码质量问题 - 注释掉的代码应该删除
    }
}
