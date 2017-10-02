package com.kontiki.saml.dao;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;

import com.kontiki.saml.audit.AuditMonitor;
import com.kontiki.saml.crypto.CryptTools;
import com.kontiki.saml.crypto.IdPKeyPair;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

@Configuration
@MapperScan("com.kontiki.saml.dao.mapper")
public class AppConfig {

	private final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	private static final String JDBC_DB_URL_PATTERN = "jdbc:mysql://%s:%s/%s";
	private static final String CONFIG_LOGBACK_FILE = "/config/logback.xml";
	private static final String RES_LOGBACK_TEMPLATE = "logback-template.xml";
	private static final String VAR_LOGBACK_TEMPLATE_INSTALL_FOLDER = "#\\{install\\.folder\\}";
	private static final String IDP_CERT_FILE = "/config/idpServer.crt";
	private static final String IDP_KEY_FILE = "/config/idpServer.key";

	@PostConstruct
	public void init() {
		// assume SLF4J is bound to logback in the current environment
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			String installFolder = readJndiPropRequired("install.folder");
			if (!StringUtils.isEmpty(installFolder)) {
				getIdPKeyPair().init(installFolder + IDP_CERT_FILE, installFolder + IDP_KEY_FILE);
				
				File f = new File(installFolder + CONFIG_LOGBACK_FILE);
				if (!f.exists()) {
					Path path = Paths.get(this.getClass().getClassLoader().getResource(RES_LOGBACK_TEMPLATE).toURI());
					String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
					content = content.replaceAll(VAR_LOGBACK_TEMPLATE_INSTALL_FOLDER, installFolder);
					Files.write(f.toPath(), content.getBytes(StandardCharsets.UTF_8));
				}
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(context);
				context.reset();
				configurator.doConfigure(f.getAbsolutePath());
				StatusPrinter.printInCaseOfErrorsOrWarnings(context);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	@Bean
	public DataSource getDataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("com.mysql.jdbc.Driver");
			String jdbsUrl = String.format(JDBC_DB_URL_PATTERN, readJndiPropRequired("core.db.host"),
					readJndiPropRequired("core.db.port"), readJndiPropRequired("core.db.name"));
			logger.info("JDBC URL: " + jdbsUrl);
			dataSource.setJdbcUrl(jdbsUrl);
			dataSource.setUser(readJndiPropRequired("core.db.user.name"));
			dataSource.setPassword(getCryptTools().decrypt(readJndiPropRequired("core.db.encrypted.password")));
		} catch (Exception e) {
			logger.error("Error in data source configuration!" + e.getMessage(), e);
		}
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(getDataSource());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(getDataSource());
		return sessionFactory.getObject();
	}

	private String readJndiPropRequired(String prop) throws Exception {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName(prop);
		jndiObjectFactoryBean.setResourceRef(true);
		jndiObjectFactoryBean.afterPropertiesSet();
		Object ofb = jndiObjectFactoryBean.getObject();
		if (ofb == null) {
			throw new Exception("Missing required JNDI property: " + prop);
		}
		return ofb.toString();
	}

	@Bean
	public CryptTools getCryptTools() {
		return new CryptTools();
	}

	@Bean
	public AuditMonitor getAuditMonitor() {
		return new AuditMonitor();
	}

	@Bean
	public IdPKeyPair getIdPKeyPair() {
		return new IdPKeyPair();
	}
}
