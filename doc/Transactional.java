package com.hs.bi.constants;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import java.lang.annotation.*;

/**
 * Describes a transaction attribute on an individual method or on a class.
 * 描述单个方法或类上的事务属性。
 *
 * <p>At the class level, this annotation applies as a default to all methods of
 * the declaring class and its subclasses. Note that it does not apply to ancestor
 * classes up the class hierarchy; methods need to be locally redeclared in order
 * to participate in a subclass-level annotation.
 * 在类级别，此注释作为默认应用于声明类及其子类的所有方法。
 * 注意，它并不应用于类层次结构上的祖先类;方法需要在本地重新声明，以便参与子类级注释。
 *
 * <p>This annotation type is generally directly comparable to Spring's
 * {@link org.springframework.transaction.interceptor.RuleBasedTransactionAttribute}
 * class, and in fact {@link AnnotationTransactionAttributeSource} will directly
 * convert the data to the latter class, so that Spring's transaction support code
 * does not have to know about annotations. If no rules are relevant to the exception,
 * it will be treated like
 * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute}
 * (rolling back on {@link RuntimeException} and {@link Error} but not on checked
 * exceptions).
 * 这种注释类型通常可以直接与Spring的RuleBasedTransactionAttribute类进行比较，
 * 实际上，AnnotationTransactionAttributeSource将直接将数据转换为后一个类，这样Spring的事务支持代码就不需要知道注释。
 * 如果回滚没有自定义异常，就使用RuntimeException，它将被视为DefaultTransactionAttribute
 *
 * <p>For specific information about the semantics of this annotation's attributes,
 * consult the {@link TransactionDefinition} and
 * {@link org.springframework.transaction.interceptor.TransactionAttribute} javadocs.
 * 有关此注释属性的语义的具体信息，请参阅TransactionDefinition和TransactionAttribute javadocs
 *
 *
 * TransactionDefinition：
 * 定义符合spring的事务属性的接口。
 * 基于类似于EJB CMT属性的传播行为定义。
 * 注意，除非启动了实际的新事务，否则不会应用隔离级别和超时设置。因为只有PROPAGATION_REQUIRED、
 * PROPAGATION_REQUIRES_NEW和PROPAGATION_NESTED可以导致这种情况，所以在其他情况下指定这些设置通常没有意义。
 * 此外，请注意，并非所有事务管理器都支持这些高级特性，因此在给定非默认值时可能会抛出相应的异常。
 *
 * 只读标志适用于任何事务上下文，无论是由实际资源事务支持的还是在资源级别以非事务方式操作的。
 * 在后一种情况下，该标志将只应用于应用程序中的托管资源，比如Hibernate {@code会话}。
 *
 * TransactionAttribute：
 * 此接口将回滚规范添加到TransactionDefinition。
 * 因为自定义回滚只有在AOP中才能实现，所以它驻留在与AOP相关的事务子包中。
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 1.2
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute
 * @see org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    /**
     * Alias for {@link #transactionManager}.
     * @see #transactionManager
     */
    @AliasFor("transactionManager")
    String value() default "";

    /**
     * A <em>qualifier</em> value for the specified transaction.
     * <p>May be used to determine the target transaction manager,
     * matching the qualifier value (or the bean name) of a specific
     * {@link org.springframework.transaction.PlatformTransactionManager}
     * bean definition.
     * @since 4.2
     * @see #value
     */
    @AliasFor("value")
    String transactionManager() default "";

    /**
     * The transaction propagation type.
     * <p>Defaults to {@link Propagation#REQUIRED}.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * The transaction isolation level.
     * <p>Defaults to {@link Isolation#DEFAULT}.
     * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
     * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
     * transactions. Consider switching the "validateExistingTransactions" flag to
     * "true" on your transaction manager if you'd like isolation level declarations
     * to get rejected when participating in an existing transaction with a different
     * isolation level.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * The timeout for this transaction (in seconds).
     * <p>Defaults to the default timeout of the underlying transaction system.
     * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
     * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
     * transactions.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
     */
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    /**
     * A boolean flag that can be set to {@code true} if the transaction is
     * effectively read-only, allowing for corresponding optimizations at runtime.
     * <p>Defaults to {@code false}.
     * <p>This just serves as a hint for the actual transaction subsystem;
     * it will <i>not necessarily</i> cause failure of write access attempts.
     * A transaction manager which cannot interpret the read-only hint will
     * <i>not</i> throw an exception when asked for a read-only transaction
     * but rather silently ignore the hint.
     * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
     */
    boolean readOnly() default false;

    /**
     * Defines zero (0) or more exception {@link Class classes}, which must be
     * subclasses of {@link Throwable}, indicating which exception types must cause
     * a transaction rollback.
     * <p>By default, a transaction will be rolling back on {@link RuntimeException}
     * and {@link Error} but not on checked exceptions (business exceptions). See
     * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)}
     * for a detailed explanation.
     * <p>This is the preferred way to construct a rollback rule (in contrast to
     * {@link #rollbackForClassName}), matching the exception class and its subclasses.
     * <p>Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}.
     * @see #rollbackForClassName
     * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}), indicating which exception types must cause
     * a transaction rollback.
     * <p>This can be a substring of a fully qualified class name, with no wildcard
     * support at present. For example, a value of {@code "ServletException"} would
     * match {@code javax.servlet.ServletException} and its subclasses.
     * <p><b>NB:</b> Consider carefully how specific the pattern is and whether
     * to include package information (which isn't mandatory). For example,
     * {@code "Exception"} will match nearly anything and will probably hide other
     * rules. {@code "java.lang.Exception"} would be correct if {@code "Exception"}
     * were meant to define a rule for all checked exceptions. With more unusual
     * {@link Exception} names such as {@code "BaseBusinessException"} there is no
     * need to use a FQN.
     * <p>Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}.
     * @see #rollbackFor
     * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
     */
    String[] rollbackForClassName() default {};

    /**
     * Defines zero (0) or more exception {@link Class Classes}, which must be
     * subclasses of {@link Throwable}, indicating which exception types must
     * <b>not</b> cause a transaction rollback.
     * <p>This is the preferred way to construct a rollback rule (in contrast
     * to {@link #noRollbackForClassName}), matching the exception class and
     * its subclasses.
     * <p>Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}.
     * @see #noRollbackForClassName
     * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    /**
     * Defines zero (0) or more exception names (for exceptions which must be a
     * subclass of {@link Throwable}) indicating which exception types must <b>not</b>
     * cause a transaction rollback.
     * <p>See the description of {@link #rollbackForClassName} for further
     * information on how the specified names are treated.
     * <p>Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}.
     * @see #noRollbackFor
     * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
     */
    String[] noRollbackForClassName() default {};

}