package com.github.jmpjct.plugin.example;

/*
 * Example plugin. Return a fake result set for every query
 */

import java.util.Date;

import com.github.jmpjct.mysql.proto.*;
import org.apache.log4j.Logger;
import com.github.jmpjct.plugin.Base;
import com.github.jmpjct.Engine;

public class ResultSetExample extends Base {

    public void init(Engine context) {
        this.logger = Logger.getLogger("Plugin.Example.ResultSetExample");
    }
    
    public void read_query(Engine context) {
        this.logger.info("Plugin->read_query");
        System.out.println(context.query);


        switch (Packet.getType(context.buffer.get(context.buffer.size() - 1))) {
            case Flags.COM_QUIT:
                this.logger.info("-> COM_QUIT");
                break;

            // Extract out the new default schema
            case Flags.COM_INIT_DB:
                this.logger.info("-> USE "+context.schema);
                break;

            // Query
            case Flags.COM_QUERY:
                this.logger.info("-> "+context.query);
                break;

            default:
                this.logger.debug("Packet is "+Packet.getType(context.buffer.get(context.buffer.size()-1))+" type.");
                break;
        }
        context.buffer_result_set();
//        ResultSet rs = new ResultSet();
//
//        Column col = new Column("Fake Data");
//        rs.addColumn(col);
//
//        rs.addRow(new Row("1"));
//
//        context.clear_buffer();
//        context.buffer = rs.toPackets();
//        context.nextMode = Flags.MODE_SEND_QUERY_RESULT;
    }
}
