
package com.adr.dataclient.links;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;
import com.adr.data.route.ReducerDataIdentity;
import com.adr.data.route.ReducerDataLink;
import com.adr.data.route.ReducerQueryIdentity;
import com.adr.data.route.ReducerQueryLink;
import com.adr.data.sql.SQLQueryLink;
import com.adr.data.security.SecureCommands;
import com.adr.data.security.jwt.ReducerDataJWTAuthorization;
import com.adr.data.security.jwt.ReducerDataJWTVerify;
import com.adr.data.security.jwt.ReducerQueryJWTAuthorization;
import com.adr.data.security.jwt.ReducerJWTCurrentUser;
import com.adr.data.security.jwt.ReducerJWTLogin;
import com.adr.data.security.jwt.ReducerQueryJWTVerify;
import com.adr.data.sql.SQLDataLink;
import com.adr.data.sql.SQLEngine;
import com.adr.data.sql.Sentence;
import com.adr.data.sql.SentenceView;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author adrian
 */
public class SampleQueryLink  {

    private final static Logger LOG = Logger.getLogger(SampleQueryLink.class.getName());

    private final DataSource cpds;
    private final SQLEngine engine;
    
    private QueryLink querylink;
    private DataLink datalink;
    
    public SampleQueryLink() {

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/hellodb");
        config.setUsername("tad");
        config.setPassword("tad");

        cpds = new HikariDataSource(config);
        engine = SQLEngine.POSTGRESQL;

        LOG.log(Level.INFO, "Database engine = {0}", engine.toString());
    }

    private QueryLink createQueryLink() {
        Sentence[] morequeries = new Sentence[]{
            new SentenceView(
            "TEST_USERNAME_VIEW",
            "SELECT ID, NAME, DISPLAYNAME, IMAGE FROM USERNAME WHERE VISIBLE = TRUE AND ACTIVE = TRUE ORDER BY NAME")
        };
        QueryLink querylink = new SQLQueryLink(cpds, engine, concatenate(SecureCommands.QUERIES, morequeries));   

        return new ReducerQueryLink(
                new ReducerQueryJWTVerify("secret".getBytes(StandardCharsets.UTF_8)),
                new ReducerJWTLogin(querylink, "secret".getBytes(StandardCharsets.UTF_8), 500000),
                new ReducerJWTCurrentUser(),
                new ReducerQueryJWTAuthorization(querylink, new HashSet<>(Arrays.asList("USERNAME_VISIBLE_QUERY")), new HashSet<>(Arrays.asList("authenticatedres"))),
                new ReducerQueryIdentity(querylink));
    }

    private DataLink createDataLink() {
        QueryLink querylink = new SQLQueryLink(cpds, engine, SecureCommands.QUERIES);
        DataLink datalink = new SQLDataLink(cpds, engine, SecureCommands.COMMANDS);
        
        return new ReducerDataLink(
                new ReducerDataJWTVerify("secret".getBytes(StandardCharsets.UTF_8)),
                new ReducerDataJWTAuthorization(querylink, new HashSet<>(Arrays.asList("USERNAME_VISIBLE_QUERY")), new HashSet<>(Arrays.asList("authenticatedres"))),
                new ReducerDataIdentity(datalink));
    }

    public void create() {
        querylink = createQueryLink();
        datalink = createDataLink();
    }

    public void destroy() {
        querylink = null;
        datalink = null;
    }

    public QueryLink getQueryLink() {
        return querylink;
    }

    public DataLink getDataLink() {
        return datalink;
    }
    
    private <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
