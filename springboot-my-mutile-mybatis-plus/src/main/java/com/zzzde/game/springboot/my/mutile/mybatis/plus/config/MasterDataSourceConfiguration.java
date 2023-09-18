package com.zzzde.game.springboot.my.mutile.mybatis.plus.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author zzzde
 * @date 2022/12/6
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.master")
@MapperScan(basePackages = "com.zzzde.game.springboot.my.mutile.mybatis.plus.dao.master", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfiguration {

    @Value("${spring.datasource.master.url}")
    protected String url;
    @Value("${spring.datasource.master.username}")
    protected String username;
    @Value("${spring.datasource.master.password}")
    protected String password;
    protected Class<? extends Log> logImpl;

    private static final String MAPPER_LOCATION = "classpath*:/mapper/master/*Mapper.xml";
    private static final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private static final int initialSize = 10;
    private static final int minIdle = 10;
    private static final int maxActive = 80;
    private static final long maxWait = 60000;
    private static final long timeBetweenEvictionRunsMillis = 60000;
    private static final long minEvictableIdleTimeMillis = 300000;
    private static final String validationQuery = "SELECT 1";
    private static final boolean testWhileIdle = true;
    private static final boolean testOnBorrow = false;
    private static final boolean testOnReturn = false;
    private static final boolean poolPreparedStatements = false;

    /**
     * 分页插件
     */
    @Bean(name = "masterMybatisPlusInterceptor")
    public MybatisPlusInterceptor masterMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件: PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全表更新与删除插件: BlockAttackInnerInterceptor
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 数据源
     */
    @Bean(name = "masterDataSource")
    // 读取spring.datasource.master前缀的配置文件映射成对应的配置对象
    public DataSource dataSource() {
        System.out.println("----druid 连接池 start ");
        DruidDataSource druid = new DruidDataSource();
        // 监控统计拦截的filters
//        druid.setFilters(filters);

        // 配置基本属性
        druid.setDriverClassName(driverClassName);
        druid.setUsername(username);
        druid.setPassword(password);
        druid.setUrl(url);
        //初始化时建立物理连接的个数
        druid.setInitialSize(initialSize);
        //最大连接池数量
        druid.setMaxActive(maxActive);
        //最小连接池数量
        druid.setMinIdle(minIdle);
        //获取连接时最大等待时间, 单位毫秒。
        druid.setMaxWait(maxWait);
        //间隔多久进行一次检测, 检测需要关闭的空闲连接
        druid.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //一个连接在池中最小生存的时间
        druid.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //用来检测连接是否有效的sql
        druid.setValidationQuery(validationQuery);
        //建议配置为true, 不影响性能, 并且保证安全性。
        druid.setTestWhileIdle(testWhileIdle);
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        druid.setTestOnBorrow(testOnBorrow);
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        druid.setTestOnReturn(testOnReturn);
        //是否缓存preparedStatement, 也就是PSCache, oracle设为true, mysql设为false
        druid.setPoolPreparedStatements(poolPreparedStatements);
        System.out.println("----druid 连接池 over ");
        return druid;
    }

    /**
     * 事务管理器，Primary注解作用同上
     */
    @Bean(name = "masterTransactionManager")
    @Primary
    public PlatformTransactionManager dataSourceTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 创建Mybatis的连接会话工厂实例
     */
    @Primary
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
        final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        // 设置数据源bean
        sessionFactory.setDataSource(masterDataSource);
        // 设置mybaits-plus配置
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        sessionFactory.setConfiguration(mybatisConfiguration());
        sessionFactory.setPlugins(mybatisPlusInterceptor());
        sessionFactory.setGlobalConfig(globalConfig());
        return sessionFactory.getObject();
    }

    /**
     * 默认 MybatisConfiguration
     */
    public MybatisConfiguration mybatisConfiguration() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setLogImpl(logImpl);
        return mybatisConfiguration;
    }

    /**
     * 默认 MybatisGlobalConfig
     */
    protected GlobalConfig globalConfig() {
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        // 自动填充数据: MetaObjectHandler
        // globalConfig.setMetaObjectHandler(new YuguMetaHandler());
        return globalConfig.setBanner(false);
    }

    /**
     * Plus插件
     */
    protected MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件: PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全表更新与删除插件: BlockAttackInnerInterceptor
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }
}
