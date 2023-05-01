package br.com.alura.bytebank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    //Conexao com banco de dados
    //alterei a url copiando diretamente do MYSQL Workbench pq deu erro
    public Connection recuperarConexao(){
        try {
            return createDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private HikariDataSource createDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/byte_bank");
        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10); //quantidade de conexoes que permito que fiquem abertas para serem reutilizadas.

        return new HikariDataSource(config);
    }
}
