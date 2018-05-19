package rest.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.config.QPHandler;

/**
 *
 * @author vitor, débora
 */
@RestController
@RequestMapping("/query_interface")
public class QPController {
    
    @Autowired
    private QPHandler qpHandler;
    

    @PostMapping(value = "/{df_tag}/{df_id}")
    public String dataflow(@PathVariable("df_tag") String dataflowTag, 
            @PathVariable("df_id") Integer dataflowID, 
            @RequestBody String message) throws UnsupportedEncodingException {
        
            String reqParam =  URLDecoder.decode(message, "UTF-8");
            reqParam = reqParam.substring(8);
            
            String message1 = "mapping(physical)\n" +
                              "source(osolversimulationtransport)\n" +
                              "target(oline0extraction;omeshwriter)\n" +
                              "projection(osolversimulationtransport.time;oline0extraction.points0;oline0extraction.points1;oline0extraction.points2;oline0extraction.s)\n" +
                              "selection(osolversimulationtransport.time < 0.5;oline0extraction.s > 0.1)";
            
            String message2 = "mapping(physical)\n" +
                            "source(osolversimulationflow)\n" +
                            "target(ovisualization;omeshwriter)\n" +
                            "projection(osolversimulationflow.time;osolversimulationflow.flow_final_linear_residualosolversimulationflow.flow_norm_delta_u)\n" +
                            "selection(osolversimulationflow.time > 0;osolversimulationflow.time < 2;osolversimulationflow.r = 0)";
            
            String message3 = "mapping(logical)\n" +
                            "source(osolversimulationflow)\n" +
                            "target(ovisualization;omeshwriter)\n" +
                            "projection(osolversimulationflow.time;osolversimulationflow.flow_final_linear_residual;osolversimulationflow.flow_norm_delta_u;osolversimulationtransport.trans_final_linear_residual;osolversimulationtransport.transport_norm_delta_u;ovisualization.png;omeshwriter.xdmf)\n" +
                            "selection(osolversimulationflow.time > 0;osolversimulationflow.time < 2;osolversimulationflow.r = 0)";
            
            String message4 = "mapping(hybrid)\n" +
                            "source(osolversimulationflow)\n" +
                            "target(ovisualization;omeshwriter)\n" +
                            "projection(osolversimulationflow.time;osolversimulationflow.flow_final_linear_residual;osolversimulationflow.flow_norm_delta_u;osolversimulationtransport.trans_final_linear_residual;osolversimulationtransport.transport_norm_delta_u;ovisualization.png;omeshwriter.xdmf)\n" +
                            "selection(osolversimulationflow.time > 0;osolversimulationflow.time < 2;osolversimulationflow.r = 0)";
            
            String message5 = "mapping(logical)\n" +
                              "source(osolversimulationtransport)\n" +
                              "target(oline0extraction;omeshwriter)\n" +
                              "projection(osolversimulationtransport.time;oline0extraction.points0;oline0extraction.points1;oline0extraction.points2;oline0extraction.s)\n" +
                              "selection(osolversimulationtransport.time < 0.5;oline0extraction.s > 0.1)";            
            
            String message6 = "mapping(hybrid)\n" +
                              "source(osolversimulationtransport)\n" +
                              "target(oline0extraction;omeshwriter)\n" +
                              "projection(osolversimulationtransport.time;oline0extraction.points0;oline0extraction.points1;oline0extraction.points2;oline0extraction.s)\n" +
                              "selection(osolversimulationtransport.time < 0.5;oline0extraction.s > 0.1)";                    
                      
            String message8 = "mapping(physical)\n" +
                            "source(osolversimulationtranport)\n" +
                            "target(osolversimulationtransport)\n" +
                            "projection(osolversimulationtransport.time)\n" +
                            "selection(osolversimulationtransport.time < 0.5)";
            
            String message9 = "mapping(physical)\n" +
                              "source(ogetmaximumiterationstotransport)\n" +
                              "target(oline0extraction;omeshwriter)\n" +
                              "projection(ogetmaximumiterationstotransport.dt;osolversimulationtransport.time;oline0extraction.points0;oline0extraction.points1;oline0extraction.points2;oline0extraction.s;)\n" +
                              "selection(osolversimulationtransport.time < 0.5;oline0extraction.s > 0.1;)";
            
            return qpHandler.runQuery(dataflowTag, dataflowID, reqParam);
    }
}

