/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import ortus.web.dto.restReq;
import ortus.web.resources.fanartResource;
import ortus.web.resources.utils;

/**
 *
 * @author jphipps
 */
public class apiService extends HttpServlet {
    Logger log = Logger.getLogger(this.getClass());
   
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setStatus(200);

        restReq restapi = new restReq(request.getPathInfo());

        if ( ! restapi.isValid()) {
            response.setStatus(500);
            return;
        }

        if ( restapi.getService().equalsIgnoreCase("fanart")) {
            log.trace("API: Fanart");
            fanartResource fr = new fanartResource();

            if ( restapi.getServiceType().equalsIgnoreCase("poster")) {
                log.trace("API: Fanart : Poster");

                if ( restapi.getArgs().isEmpty())
                    utils.returnFile(response, fr.getPosterCover(restapi.getMediaid()));
                if ( restapi.getArgs().equalsIgnoreCase("thumb"))
                    utils.returnFile(response, fr.getPosterThumb(restapi.getMediaid()));
                if ( restapi.getArgs().equalsIgnoreCase("cover"))
                    utils.returnFile(response, fr.getPosterCover(restapi.getMediaid()));
                if ( restapi.getArgs().equalsIgnoreCase("high"))
                    utils.returnFile(response, fr.getPosterHigh(restapi.getMediaid()));
            }
            if ( restapi.getServiceType().equalsIgnoreCase("background")) {
                if ( restapi.getArgs().isEmpty())
                    utils.returnFile(response, fr.getBackgroundCover(restapi.getMediaid()));                
                if ( restapi.getArgs().equalsIgnoreCase("thumb"))
                    utils.returnFile(response, fr.getBackgroundThumb(restapi.getMediaid()));
                if ( restapi.getArgs().equalsIgnoreCase("cover"))
                    utils.returnFile(response, fr.getBackgroundCover(restapi.getMediaid()));
                if ( restapi.getArgs().equalsIgnoreCase("high"))
                    utils.returnFile(response, fr.getBackgroundHigh(restapi.getMediaid()));
            }
            if ( restapi.getServiceType().equalsIgnoreCase("banner")) {
                utils.returnFile(response, fr.getBanner(restapi.getMediaid()));
            }

        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
