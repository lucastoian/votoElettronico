package com.example.votoelettronico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDaoImpl implements UtenteDao{
    //credenziali per accedere al db hostato su heroku
    private static final String url = "jdbc:postgresql://ec2-34-255-134-200.eu-west-1.compute.amazonaws.com:5432/du00brnb1t9ok";
    private static final String user = "ykeiygtowzihlm";
    private static final String password = "70c908bb89427bd76a11350372a669f02b455e056c3fd572f4a94d1d2b65d48c";

    private List<Utente> utenti;
    private static Encryption encryption;

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    private static void closeConnection(Connection con) throws SQLException {
        con.close();
    }

    public UtenteDaoImpl() throws Exception {
        this.utenti = new ArrayList<>();
        encryption = new Encryption("LucaDamonChiaveTopSecret");
        String query = "SELECT * FROM utente";
        Connection con= DriverManager.getConnection(url, user, password);
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet result = pst.executeQuery();

        while(result.next()){
            Utente u = new Utente(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
            utenti.add(u);
        }
        System.out.println("UtenteDao si è connesso correttamente al db");
        con.close();
    }




    @Override
    public  List<Utente> getAllUtenti() {
        return this.utenti;
    }

    @Override
    public void addUtente(Utente u) throws SQLException {
        String query = "INSERT INTO utente VALUES(?, ?, ?, ?, ?)";

        Connection con = DriverManager.getConnection(url, user, password);
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, u.getName());
        pst.setString(2, u.getSurname());
        pst.setString(3, u.getEmail());
        pst.setString(4, encryption.encrypt(u.getPassword()));
        pst.setString(5, u.getCodFiscale());

        pst.executeUpdate();

        utenti.add(u);
        System.out.println("UTENTE REGISTRATO CON SUCCESSO");
        con.close();


    }

    @Override
    public void deleteUtente() {

    }

    @Override
    public Boolean loginUtente(String email, String password) throws SQLException {
        Connection con = openConnection();
        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, email);
        pst.setString(2, encryption.encrypt(password));
        ResultSet result = pst.executeQuery();

        while(result.next()){
            con.close();
            return true;
        }
        con.close();
        return false;
    }
}