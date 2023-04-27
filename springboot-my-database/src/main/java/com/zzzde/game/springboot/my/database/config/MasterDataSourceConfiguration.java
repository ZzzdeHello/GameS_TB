package com.zzzde.game.springboot.my.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author zzzde
 * @date 2022/12/6
 */
@Configuration
@MapperScan(basePackages = "com.zzzde.game.springboot.my.database.dao.master",sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfiguration {

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
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource dataSource() {
        System.out.println("----SpringBoot-Hikari 连接池 start ");
        DataSource build = DataSourceBuilder.create().build();
        System.out.println("----SpringBoot-Hikari 连接池 over ");
        return build;
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
     * session工厂，Primary注解作用同上
     */

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/master/*.xml"));
        return sessionFactoryBean.getObject();
    }
}
