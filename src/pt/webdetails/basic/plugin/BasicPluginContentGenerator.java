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
package pt.webdetails.basic.plugin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.pentaho.platform.util.messages.LocaleHelper;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

public class BasicPluginContentGenerator extends BaseContentGenerator {

  protected static Log logger = LogFactory.getLog( BasicPluginLifecycleListener.class );
  private static final String PATH_PARAMETER_ID = "path";
  private static final String MIME_HTML = "text/html";

  private IUnifiedRepository repository;

  public BasicPluginContentGenerator( IUnifiedRepository repository ){
    setRepository( repository );
  }

  @Override public Log getLogger() {
    return logger;
  }

  @Override public void createContent() throws Exception {
    info( "createContent()" );

    IParameterProvider pathParams = parameterProviders.get( PATH_PARAMETER_ID );

    RepositoryFile file;
    String urlEncodedFilePath;

    if( pathParams != null
        && !StringUtils.isEmpty( ( urlEncodedFilePath = pathParams.getStringParameter( Constants.JaxRs.PATH, null ) ) ) ) {

      String filePath = URLDecoder.decode( urlEncodedFilePath, LocaleHelper.UTF_8 );

      getResponse().setContentType( MIME_HTML );
      getResponse().setHeader( "Cache-Control", "no-cache" );

      if ( ( file = getRepository().getFile( filePath ) ) == null ) {
        throw new WebApplicationException( Response.Status.NOT_FOUND );
      }

      InputStream content = null;

      try {

        SimpleRepositoryFileData data = getRepository().getDataForRead( file.getId(), SimpleRepositoryFileData.class );

        writeOutAndFlush( getResponse().getOutputStream(), data.getInputStream() );

      } finally {
        IOUtils.closeQuietly( content );
      }
    }
  }

  public IUnifiedRepository getRepository() {
    return repository;
  }

  public void setRepository( IUnifiedRepository repository ) {
    this.repository = repository;
  }

  protected HttpServletResponse getResponse(){
    return (HttpServletResponse) parameterProviders.get( PATH_PARAMETER_ID ).getParameter( "httpresponse" );
  }

  private void writeOutAndFlush( OutputStream out, InputStream data ) {
    writeOut( out, data );
    flush( out );
  }

  private void writeOut( OutputStream out, InputStream data ){
    try {
      IOUtils.copy( data, out );
    } catch (IOException ex){
      getLogger().error( ex );
    }
  }

  private void flush( OutputStream out ) {
    try {
      if ( out != null ) {
        out.flush();
      }
    } catch ( IOException ex ) {
      getLogger().error( ex );
    }
  }
}
