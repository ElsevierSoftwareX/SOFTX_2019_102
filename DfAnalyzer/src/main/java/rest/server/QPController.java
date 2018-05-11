package rest.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.config.QPHandler;

/**
 *
 * @author vitor
 */
@RestController
@RequestMapping("/query_interface")
public class QPController {
    
    @Autowired
    private QPHandler qpHandler;

    @PostMapping(value = "/{df_tag}/{df_id}")
    public String dataflow(@PathVariable("df_tag") String dataflowTag, 
            @PathVariable("df_id") Integer dataflowID, 
            @RequestBody String message) {
        return qpHandler.runQuery(dataflowTag, dataflowID, message);
    }

}
