package com.zhixindu.apply.core.app;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhixindu.commons.pagination.PaginationInterceptor;
import com.zhixindu.commons.repository.DefaultPageRepository;
import com.zhixindu.commons.repository.PageRepository;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author SteveGuo
 * @version 1.0
 * @date 2017/3/3
 * @description
 */
@Configuration
@MapperScan({"com.zhixindu.apply.core.*.dao"})
@PropertySource(value = {"classpath:/apply.properties"})
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${connection.url}")
    private String url;
    @Value("${connection.username}")
    private String username;
    @Value("${connection.password}")
    private String password;
    @Value("${druid.initialSize}")
    private int initialSize;
    @Value("${druid.minIdle}")
    private int minIdle;
    @Value("${druid.maxActive}")
    private int maxActive;
    @Value("${druid.maxWait}")
    private int maxWait;
    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;
    @Value("${druid.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;
    @Value("${druid.validationQuery}")
    private String validationQuery;
    @Value("${druid.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${druid.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${druid.testOnReturn}")
    private boolean testOnReturn;
    @Value("${druid.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${druid.filters}")
    private String filters;
    @Value("${druid.connectionProperties}")
    private String connectionProperties;

    @Bean
    public DataSource dataSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        /** 基本属性 url、user、password */
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        /** 配置初始化大小、最小、最大 */
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        /** 配置获取连接等待超时的时间 */
        dataSource.setMaxWait(maxWait);
        /** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        /** 配置一个连接在池中最小生存的时间，单位是毫秒 */
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        /** 关闭PSCache，并且指定每个连接上PSCache的大小，mysql可以配置为false*/
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        /** 配置监控统计拦截的filters */
        dataSource.setFilters(filters);
        /** 配置数据库密码加密 */
        dataSource.setConnectionProperties(connectionProperties);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setTypeAliasesPackage("com.zhixindu.apply.facade.*.bo");
        Interceptor paginationInterceptor = new PaginationInterceptor();
        Properties properties = new Properties();
        properties.setProperty("stmtIdRegex", "*.*ByPage");
        properties.setProperty("dialect", "mysql");
        paginationInterceptor.setProperties(properties);
        sessionFactory.setPlugins(new Interceptor[]{paginationInterceptor});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/**/*Mapper.xml"));
        return sessionFactory;
    }

    @Bean
    public PageRepository pageRepository() throws Exception {
        return new DefaultPageRepository(new SqlSessionTemplate(sqlSessionFactory().getObject()));
    }

}
