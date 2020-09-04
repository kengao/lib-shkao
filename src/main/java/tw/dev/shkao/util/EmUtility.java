/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.util;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author kengao
 */
public class EmUtility {
    
    // ---------------------------------------------------------------------------
    public static void processQueryParameter(Query query, List argList) {

        if (argList == null || argList.isEmpty()) {
            return;
        }

        processQueryParameter(query, argList.toArray());

    }

    public static void processQueryParameter(Query query, Object... args) {

        if (args == null || args.length == 0) {
            return;
        }

        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (arg instanceof java.sql.Date) {
                query.setParameter(i + 1, (Date) arg, TemporalType.DATE);
            } else if (arg instanceof java.sql.Timestamp || arg instanceof java.util.Date) {
                query.setParameter(i + 1, (Date) arg, TemporalType.TIMESTAMP);
            } else if (arg instanceof Enum) {
                query.setParameter(i + 1, arg.toString());
            } else {
                query.setParameter(i + 1, arg);
            }
        }
    }


}
