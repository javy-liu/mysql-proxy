package org.oyach.mysql.proxy.plugin.example;

/*
 * Example plugin. Return a fake result set for every query
 */


import org.oyach.mysql.protocol.Column;
import org.oyach.mysql.protocol.Flags;
import org.oyach.mysql.protocol.ResultSet;
import org.oyach.mysql.protocol.Row;
import org.oyach.mysql.proxy.Engine;
import org.oyach.mysql.proxy.plugin.Base;

public class ResultSetExample extends Base {

    public void init(Engine context) {

    }
    
    public void read_query(Engine context) {
        logger.info("Plugin->read_query");
        
        ResultSet rs = new ResultSet();
        
        Column col = new Column("Fake Data");
        rs.addColumn(col);
        
        rs.addRow(new Row("1"));
        
        context.clear_buffer();
        context.buffer = rs.toPackets();
        context.nextMode = Flags.MODE_SEND_QUERY_RESULT;
    }
}
