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
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.utils.MimeTypes;
import pt.webdetails.cpf.utils.PluginIOUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BasicPluginContentGenerator extends SimpleContentGenerator {

  private IUnifiedRepository repository = PentahoSystem.get( IUnifiedRepository.class );

  @Override public void createContent() throws Exception {
    info( "createContent()" );

    String path = getPathParameterAsString( Constants.JaxRs.PATH, null );

    RepositoryFile file;

    getResponse().setContentType( MimeTypes.HTML );
    getResponse().setHeader( "Cache-Control", "no-cache" );

    if( ( file = repository.getFile( path ) ) != null ) {

      SimpleRepositoryFileData data = repository.getDataForRead( file.getId() , SimpleRepositoryFileData.class );

      PluginIOUtils.writeOutAndFlush( getResponse().getOutputStream(), IOUtils.toString( data.getInputStream() ) );
      return;
    }

    throw new WebApplicationException( Response.Status.NOT_FOUND );
  }

  @Override public String getPluginName() {
    return Constants.PLUGIN_ID;
  }
}
