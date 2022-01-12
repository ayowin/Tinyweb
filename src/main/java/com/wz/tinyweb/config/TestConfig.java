package com.wz.tinyweb.config;


import com.wz.tinyweb.core.Configuration;
import com.wz.tinyweb.core.Inject;

@Configuration
public class TestConfig {

    public static class DataSource{

    }

    private static class Connection{
        private DataSource dataSource;
        public Connection(DataSource dataSource){
            this.dataSource = dataSource;
        }
    }

    @Inject
    public DataSource dataSource(){
        DataSource dataSource = new DataSource();
        return dataSource;
    }

    @Inject
    public Connection connection(DataSource dataSource){
        Connection connection = new Connection(dataSource);
        return connection;
    }

}
