/*!
* Copyright 2002 - 2015 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/
package pt.webdetails.basic.plugin.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import pt.webdetails.basic.plugin.Constants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * rest entry endpoint is 'plugin/basic-plugin/api/'
 * @see org.pentaho.platform.web.servlet.JAXRSPluginServlet
 */
@Path( Constants.PLUGIN_ID + "/api/" )
public class BasicPluginRestApi {

  protected static Log logger = LogFactory.getLog( BasicPluginRestApi.class );

  private IUnifiedRepository repository;

  public BasicPluginRestApi( IUnifiedRepository repository ){
    setRepository( repository );
  }

  @GET
  @Path( Constants.JaxRs.READY /* REST API '/ready' endpoint */ )
  @Produces( { Constants.MIME_TEXT_PLAIN } )
  public String ready() {
    logger.info( "ready()" );
    return Boolean.toString( true );
  }

  @GET
  @Path( Constants.JaxRs.VIEW /* REST API '/view' endpoint */ )
  public Response content( @QueryParam( Constants.JaxRs.PATH ) String path ) {
    logger.info( "view()" );

    RepositoryFile file;
    InputStream content = null;

    try {

      if( ( file = getRepository().getFile( path ) ) == null ) {
        logger.warn( "file not found in path " + path );
        return Response.status( Response.Status.NOT_FOUND ).build();
      }

      SimpleRepositoryFileData data = getRepository().getDataForRead( file.getId(), SimpleRepositoryFileData.class );
      content = data.getInputStream();

      return Response.ok( IOUtils.toString( content ), MediaType.TEXT_PLAIN_TYPE ).build();

    } catch ( Exception e ) {
      logger.error( e );
      return Response.serverError().build();

    } finally {
      IOUtils.closeQuietly( content );
    }
  }

  public IUnifiedRepository getRepository() {
    return repository;
  }

  public void setRepository( IUnifiedRepository repository ) {
    this.repository = repository;
  }

}
