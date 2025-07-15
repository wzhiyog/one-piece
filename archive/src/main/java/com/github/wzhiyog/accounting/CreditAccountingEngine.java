package com.github.wzhiyog.accounting;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

// 核心实体类
public class CreditAccountingEngine {

    // 科目类型枚举
    public enum AccountSubjectType {
        ASSET, LIABILITY, EQUITY, INCOME, EXPENSE
    }

    // 分录方向枚举
    public enum EntryDirection {
        DEBIT, CREDIT
    }

    // 交易状态枚举
    public enum TransactionStatus {
        INIT, PROCESSING, SUCCESS, FAILED, REVERSED
    }

    // 会计科目实体
    public static class AccountSubject {
        private final String code;
        private final String name;
        private final AccountSubjectType type;
        private BigDecimal balance = BigDecimal.ZERO;
        private final ReentrantLock lock = new ReentrantLock();

        public AccountSubject(String code, String name, AccountSubjectType type) {
            this.code = code;
            this.name = name;
            this.type = type;
        }

        // 更新余额（线程安全）
        public void updateBalance(EntryDirection direction, BigDecimal amount) {
            lock.lock();
            try {
                switch (type) {
                    case ASSET:
                    case EXPENSE:
                        if (direction == EntryDirection.DEBIT) {
                            balance = balance.add(amount);
                        } else {
                            balance = balance.subtract(amount);
                        }
                        break;
                    case LIABILITY:
                    case EQUITY:
                    case INCOME:
                        if (direction == EntryDirection.CREDIT) {
                            balance = balance.add(amount);
                        } else {
                            balance = balance.subtract(amount);
                        }
                        break;
                }
            } finally {
                lock.unlock();
            }
        }

        // Getters
        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public AccountSubjectType getType() {
            return type;
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }

    // 交易流水实体
    public static class TransactionFlow {
        private final String transactionNo;
        private final String transactionType;
        private final BigDecimal amount;
        private TransactionStatus status;
        private final LocalDateTime createTime;
        private LocalDateTime updateTime;

        public TransactionFlow(String transactionNo, String transactionType, BigDecimal amount) {
            this.transactionNo = transactionNo;
            this.transactionType = transactionType;
            this.amount = amount;
            this.status = TransactionStatus.INIT;
            this.createTime = LocalDateTime.now();
        }

        // 更新状态
        public void updateStatus(TransactionStatus status) {
            this.status = status;
            this.updateTime = LocalDateTime.now();
        }

        // Getters
        public String getTransactionNo() {
            return transactionNo;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public TransactionStatus getStatus() {
            return status;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }
    }

    // 会计分录实体
    public static class AccountingEntry {
        private final String entryId;
        private final String transactionNo;
        private final String accountCode;
        private final EntryDirection direction;
        private final BigDecimal amount;
        private final LocalDateTime createTime;

        public AccountingEntry(String transactionNo, String accountCode, EntryDirection direction, BigDecimal amount) {
            this.entryId = UUID.randomUUID().toString();
            this.transactionNo = transactionNo;
            this.accountCode = accountCode;
            this.direction = direction;
            this.amount = amount;
            this.createTime = LocalDateTime.now();
        }

        // Getters
        public String getEntryId() {
            return entryId;
        }

        public String getTransactionNo() {
            return transactionNo;
        }

        public String getAccountCode() {
            return accountCode;
        }

        public EntryDirection getDirection() {
            return direction;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    // 记账规则配置
    public static class AccountingRule {
        private final String transactionType;
        private final List<EntryRule> entryRules;

        public AccountingRule(String transactionType, List<EntryRule> entryRules) {
            this.transactionType = transactionType;
            this.entryRules = entryRules;
        }

        public List<EntryRule> getEntryRules() {
            return entryRules;
        }
    }

    // 分录规则
    public static class EntryRule {
        private final String accountCode;
        private final EntryDirection direction;
        private final BigDecimal ratio; // 金额比例，1.0表示全额

        public EntryRule(String accountCode, EntryDirection direction) {
            this(accountCode, direction, BigDecimal.ONE);
        }

        public EntryRule(String accountCode, EntryDirection direction, BigDecimal ratio) {
            this.accountCode = accountCode;
            this.direction = direction;
            this.ratio = ratio;
        }

        // Getters
        public String getAccountCode() {
            return accountCode;
        }

        public EntryDirection getDirection() {
            return direction;
        }

        public BigDecimal getRatio() {
            return ratio;
        }
    }

    // 余额历史记录
    public static class BalanceHistory {
        private final String recordId;
        private final String accountCode;
        private final BigDecimal beforeBalance;
        private final BigDecimal afterBalance;
        private final LocalDateTime changeTime;
        private final String transactionNo;

        public BalanceHistory(String accountCode, BigDecimal beforeBalance,
                              BigDecimal afterBalance, String transactionNo) {
            this.recordId = UUID.randomUUID().toString();
            this.accountCode = accountCode;
            this.beforeBalance = beforeBalance;
            this.afterBalance = afterBalance;
            this.changeTime = LocalDateTime.now();
            this.transactionNo = transactionNo;
        }

        // Getters
        public String getRecordId() {
            return recordId;
        }

        public String getAccountCode() {
            return accountCode;
        }

        public BigDecimal getBeforeBalance() {
            return beforeBalance;
        }

        public BigDecimal getAfterBalance() {
            return afterBalance;
        }

        public LocalDateTime getChangeTime() {
            return changeTime;
        }
    }

    // 会计引擎核心服务
    public static class AccountingEngineService {
        // 内存存储（生产环境应替换为数据库）
        private final Map<String, AccountSubject> subjects = new ConcurrentHashMap<>();
        private final Map<String, TransactionFlow> transactions = new ConcurrentHashMap<>();
        private final Map<String, AccountingEntry> entries = new ConcurrentHashMap<>();
        private final Map<String, AccountingRule> rules = new ConcurrentHashMap<>();
        private final List<BalanceHistory> balanceHistories = new ArrayList<>();
        private final Map<String, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

        public AccountingEngineService() {
            // 初始化科目
            initializeSubjects();
            // 初始化记账规则
            initializeRules();
        }

        private void initializeSubjects() {
            // 资产类
            addSubject(new AccountSubject("1001", "现金", AccountSubjectType.ASSET));
            addSubject(new AccountSubject("1002", "银行存款", AccountSubjectType.ASSET));
            addSubject(new AccountSubject("1101", "短期贷款", AccountSubjectType.ASSET));
            addSubject(new AccountSubject("1102", "中长期贷款", AccountSubjectType.ASSET));

            // 负债类
            addSubject(new AccountSubject("2001", "短期存款", AccountSubjectType.LIABILITY));
            addSubject(new AccountSubject("2002", "长期存款", AccountSubjectType.LIABILITY));
            addSubject(new AccountSubject("2101", "应付利息", AccountSubjectType.LIABILITY));

            // 权益类
            addSubject(new AccountSubject("3001", "实收资本", AccountSubjectType.EQUITY));
            addSubject(new AccountSubject("3002", "资本公积", AccountSubjectType.EQUITY));

            // 收入类
            addSubject(new AccountSubject("4001", "利息收入", AccountSubjectType.INCOME));
            addSubject(new AccountSubject("4002", "手续费收入", AccountSubjectType.INCOME));

            // 费用类
            addSubject(new AccountSubject("5001", "利息支出", AccountSubjectType.EXPENSE));
            addSubject(new AccountSubject("5002", "手续费支出", AccountSubjectType.EXPENSE));
            addSubject(new AccountSubject("5003", "坏账损失", AccountSubjectType.EXPENSE));
        }

        private void initializeRules() {
            // 贷款发放规则
            rules.put("LOAN_ISSUE", new AccountingRule("LOAN_ISSUE", Arrays.asList(
                    new EntryRule("1002", EntryDirection.CREDIT), // 贷：银行存款
                    new EntryRule("1101", EntryDirection.DEBIT)  // 借：短期贷款
            )));

            // 贷款还款规则
            rules.put("LOAN_REPAYMENT", new AccountingRule("LOAN_REPAYMENT", Arrays.asList(
                    new EntryRule("1002", EntryDirection.DEBIT),  // 借：银行存款
                    new EntryRule("1101", EntryDirection.CREDIT) // 贷：短期贷款
            )));

            rules.put("LOAN_REPAYMENT_FEE", new AccountingRule("LOAN_REPAYMENT_FEE", Arrays.asList(
                    new EntryRule("1002", EntryDirection.DEBIT),  // 借：银行存款
                    new EntryRule("4001", EntryDirection.CREDIT) // 贷：利息收入
            )));

            // 存款存入规则
            rules.put("DEPOSIT_RECEIVE", new AccountingRule("DEPOSIT_RECEIVE", Arrays.asList(
                    new EntryRule("1002", EntryDirection.DEBIT),  // 借：银行存款
                    new EntryRule("2001", EntryDirection.CREDIT)  // 贷：短期存款
            )));

            // 存款支取规则
            rules.put("DEPOSIT_WITHDRAW", new AccountingRule("DEPOSIT_WITHDRAW", Arrays.asList(
                    new EntryRule("2001", EntryDirection.DEBIT),  // 借：短期存款
                    new EntryRule("1002", EntryDirection.CREDIT)  // 贷：银行存款
            )));

            // 计提利息规则
            rules.put("INTEREST_ACCRUAL", new AccountingRule("INTEREST_ACCRUAL", Arrays.asList(
                    new EntryRule("2101", EntryDirection.CREDIT), // 贷：应付利息
                    new EntryRule("5001", EntryDirection.DEBIT)   // 借：利息支出
            )));

            // 坏账核销规则
            rules.put("BAD_DEBT_WRITE_OFF", new AccountingRule("BAD_DEBT_WRITE_OFF", Arrays.asList(
                    new EntryRule("1101", EntryDirection.CREDIT), // 贷：短期贷款
                    new EntryRule("5003", EntryDirection.DEBIT)   // 借：坏账损失
            )));
        }

        // 添加科目
        public void addSubject(AccountSubject subject) {
            subjects.put(subject.getCode(), subject);
            accountLocks.putIfAbsent(subject.getCode(), new ReentrantLock());
        }

        // 获取科目
        public AccountSubject getSubject(String code) {
            return subjects.get(code);
        }

        // 处理交易
        public void processTransaction(String transactionNo, String transactionType, BigDecimal amount) {
            // 1. 创建交易流水
            TransactionFlow flow = new TransactionFlow(transactionNo, transactionType, amount);
            transactions.put(transactionNo, flow);
            flow.updateStatus(TransactionStatus.PROCESSING);

            try {
                // 2. 获取记账规则
                AccountingRule rule = rules.get(transactionType);
                if (rule == null) {
                    throw new AccountingException("未找到交易类型对应的记账规则: " + transactionType);
                }

                // 3. 生成会计分录
                List<AccountingEntry> entries = generateAccountingEntries(flow, rule, amount);

                // 4. 校验借贷平衡
                validateBalance(entries);

                // 5. 更新科目余额
                updateAccountBalances(entries, transactionNo);

                // 6. 保存会计分录
                saveAccountingEntries(entries);

                // 7. 更新交易状态
                flow.updateStatus(TransactionStatus.SUCCESS);
            } catch (Exception e) {
                flow.updateStatus(TransactionStatus.FAILED);
                throw new AccountingException("交易处理失败: " + e.getMessage(), e);
            }
        }

        // 生成会计分录
        private List<AccountingEntry> generateAccountingEntries(TransactionFlow flow,
                                                                AccountingRule rule,
                                                                BigDecimal amount) {
            List<AccountingEntry> entries = new ArrayList<>();
            for (EntryRule entryRule : rule.getEntryRules()) {
                BigDecimal entryAmount = amount.multiply(entryRule.getRatio());
                AccountingEntry entry = new AccountingEntry(
                        flow.getTransactionNo(),
                        entryRule.getAccountCode(),
                        entryRule.getDirection(),
                        entryAmount
                );
                entries.add(entry);
            }
            return entries;
        }

        // 校验借贷平衡
        private void validateBalance(List<AccountingEntry> entries) {
            BigDecimal debitTotal = BigDecimal.ZERO;
            BigDecimal creditTotal = BigDecimal.ZERO;

            for (AccountingEntry entry : entries) {
                if (entry.getDirection() == EntryDirection.DEBIT) {
                    debitTotal = debitTotal.add(entry.getAmount());
                } else {
                    creditTotal = creditTotal.add(entry.getAmount());
                }
            }

            if (debitTotal.compareTo(creditTotal) != 0) {
                throw new AccountingException("借贷不平衡: 借方总额=" + debitTotal + ", 贷方总额=" + creditTotal);
            }
        }

        // 更新科目余额（带锁）
        private void updateAccountBalances(List<AccountingEntry> entries, String transactionNo) {
            // 获取所有涉及科目的锁（按固定顺序获取防止死锁）
            List<String> accountCodes = entries.stream()
                    .map(AccountingEntry::getAccountCode)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            try {
                // 按顺序加锁
                for (String code : accountCodes) {
                    ReentrantLock lock = accountLocks.get(code);
                    if (lock != null) {
                        lock.lock();
                    }
                }

                // 执行余额更新
                for (AccountingEntry entry : entries) {
                    AccountSubject subject = subjects.get(entry.getAccountCode());
                    if (subject == null) {
                        throw new AccountNotFoundException("科目不存在: " + entry.getAccountCode());
                    }

                    BigDecimal before = subject.getBalance();
                    subject.updateBalance(entry.getDirection(), entry.getAmount());
                    BigDecimal after = subject.getBalance();

                    // 记录余额变更历史
                    balanceHistories.add(new BalanceHistory(
                            subject.getCode(), before, after, transactionNo
                    ));
                }
            } finally {
                // 按逆序释放锁
                for (int i = accountCodes.size() - 1; i >= 0; i--) {
                    ReentrantLock lock = accountLocks.get(accountCodes.get(i));
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            }
        }

        // 保存会计分录
        private void saveAccountingEntries(List<AccountingEntry> entries) {
            for (AccountingEntry entry : entries) {
                this.entries.put(entry.getEntryId(), entry);
            }
        }

        // 红字冲正
        public void reverseTransaction(String originalTransactionNo) {
            TransactionFlow originalFlow = transactions.get(originalTransactionNo);
            if (originalFlow == null) {
                throw new AccountingException("原始交易不存在: " + originalTransactionNo);
            }

            if (originalFlow.getStatus() != TransactionStatus.SUCCESS) {
                throw new AccountingException("只能冲正成功的交易");
            }

            // 创建冲正交易
            String reverseNo = "REV_" + originalTransactionNo + "_" + System.currentTimeMillis();
            TransactionFlow reverseFlow = new TransactionFlow(reverseNo, "REVERSAL", originalFlow.getAmount());
            transactions.put(reverseNo, reverseFlow);
            reverseFlow.updateStatus(TransactionStatus.PROCESSING);

            try {
                // 获取原始分录
                List<AccountingEntry> originalEntries = entries.values().stream()
                        .filter(e -> e.getTransactionNo().equals(originalTransactionNo))
                        .collect(Collectors.toList());

                // 生成反向分录
                List<AccountingEntry> reverseEntries = new ArrayList<>();
                for (AccountingEntry original : originalEntries) {
                    EntryDirection reverseDirection = original.getDirection() == EntryDirection.DEBIT ?
                            EntryDirection.CREDIT : EntryDirection.DEBIT;

                    AccountingEntry reverseEntry = new AccountingEntry(
                            reverseNo,
                            original.getAccountCode(),
                            reverseDirection,
                            original.getAmount()
                    );
                    reverseEntries.add(reverseEntry);
                }

                // 更新余额
                updateAccountBalances(reverseEntries, reverseNo);

                // 保存分录
                saveAccountingEntries(reverseEntries);

                // 更新原始交易状态
                originalFlow.updateStatus(TransactionStatus.REVERSED);
                reverseFlow.updateStatus(TransactionStatus.SUCCESS);
            } catch (Exception e) {
                reverseFlow.updateStatus(TransactionStatus.FAILED);
                throw new AccountingException("冲正处理失败: " + e.getMessage(), e);
            }
        }

        // 余额健康检查
        public void checkBalanceHealth() {
            for (AccountSubject subject : subjects.values()) {
                BigDecimal balance = subject.getBalance();

                switch (subject.getType()) {
                    case ASSET:
                    case EXPENSE:
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            System.err.println("警告: 资产/费用类科目余额为负 - " +
                                    subject.getCode() + ":" + subject.getName() +
                                    " 余额=" + balance);
                        }
                        break;
                    case LIABILITY:
                    case EQUITY:
                    case INCOME:
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            System.err.println("警告: 负债/权益/收入类科目余额为负 - " +
                                    subject.getCode() + ":" + subject.getName() +
                                    " 余额=" + balance);
                        }
                        break;
                }
            }
        }

        // 打印科目余额
        public void printAccountBalances() {
            System.out.println("\n===== 科目余额表 =====");
            System.out.printf("%-10s %-20s %-15s %15s%n",
                    "科目代码", "科目名称", "科目类型", "余额");

            subjects.values().stream()
                    .sorted(Comparator.comparing(AccountSubject::getCode))
                    .forEach(subject -> {
                        System.out.printf("%-10s %-20s %-15s %,15.2f%n",
                                subject.getCode(),
                                subject.getName(),
                                subject.getType(),
                                subject.getBalance());
                    });
        }
    }

    // 自定义异常
    public static class AccountingException extends RuntimeException {
        public AccountingException(String message) {
            super(message);
        }

        public AccountingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AccountNotFoundException extends AccountingException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    // 测试主方法
    public static void main(String[] args) {
        AccountingEngineService engine = new AccountingEngineService();

        System.out.println("===== 初始化科目余额 =====");
        engine.printAccountBalances();

        System.out.println("\n===== 测试存款存入交易 =====");
        engine.processTransaction("T20230507001", "DEPOSIT_RECEIVE", new BigDecimal("1000000"));
        engine.printAccountBalances();

        System.out.println("\n===== 测试贷款发放交易 =====");
        engine.processTransaction("T20230507002", "LOAN_ISSUE", new BigDecimal("500000"));
        engine.printAccountBalances();

        System.out.println("\n===== 测试贷款还款交易 =====");
        // 本金500,000 + 利息10,000
        engine.processTransaction("T20230507003", "LOAN_REPAYMENT", new BigDecimal("500000"));
        engine.processTransaction("T202305070031", "LOAN_REPAYMENT_FEE", new BigDecimal("10000"));
        engine.printAccountBalances();

        System.out.println("\n===== 测试计提利息交易 =====");
        engine.processTransaction("T20230507004", "INTEREST_ACCRUAL", new BigDecimal("5000"));
        engine.printAccountBalances();

        System.out.println("\n===== 测试交易冲正 =====");
        engine.reverseTransaction("T20230507004");
        engine.printAccountBalances();

        System.out.println("\n===== 余额健康检查 =====");
        engine.checkBalanceHealth();
    }
}