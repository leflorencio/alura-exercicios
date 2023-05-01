package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    //Classe responsável por interagir com o banco de dados
    private Connection conn;
    //quem vai utilizar a classe DAO é a classe service
    ContaDAO(Connection connection){
        //recebemos a connection que é igual ao do construtor que contaService vai passar pra gente
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){

        //Recebemos o DadosAberturaConta extraimos os dados um cliente e uma conta
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);

        //Clausula para inserir dados no banco
        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES (?, ?, ?, ?, ?)";

        //setar os valores dos parametros
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3,dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4,dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5,dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6, true);

            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar(){
        //vamos bater no banco e ir adicionando conta por conta na lista de Set
        PreparedStatement ps;
        ResultSet resultSet;
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta WHERE esta_ativa = true";

        try {
            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                    Integer numero = resultSet.getInt(1);
                    BigDecimal saldo = resultSet.getBigDecimal(2);
                    String nome = resultSet.getString(3);
                    String cpf = resultSet.getString(4);
                    String email = resultSet.getString(5);
                    Boolean estaAtiva = resultSet.getBoolean(6);

                    DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                    Cliente cliente = new Cliente(dadosCadastroCliente);
                    contas.add(new Conta(numero, saldo, cliente, estaAtiva));
                }
                resultSet.close();
                ps.close();
            }   catch (SQLException e) {
                throw new RuntimeException();
        }
        return  contas;
    }

    public Conta listarPorNumero(Integer numero){

        String sql = "SELECT * FROM conta WHERE numero = ? and esta_ativa = true";
        PreparedStatement ps;
        ResultSet resultSet;
        Conta conta = null;

        try{
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numero);
            resultSet = ps.executeQuery();

            while(resultSet.next()){
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);

                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf,email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente, estaAtiva);
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor){
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try{
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        }catch (SQLException e){
            try {
                conn.rollback();
            } catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }

    public void deletar(Integer numeroDaConta){
        String sql = "DELETE FROM conta WHERE numero = ?";

        try{
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setInt(1, numeroDaConta);

           ps.execute();
           ps.close();
           conn.close();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numeroDaConta){
        PreparedStatement ps;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try{
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroDaConta);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        }catch (SQLException e){
                throw new RuntimeException(e);
            }
    }

}
