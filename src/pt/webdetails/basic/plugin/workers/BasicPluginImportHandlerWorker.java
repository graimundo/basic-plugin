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
package pt.webdetails.basic.plugin.workers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.repository.DomainAlreadyExistsException;
import org.pentaho.metadata.repository.DomainIdNullException;
import org.pentaho.metadata.repository.DomainStorageException;
import org.pentaho.platform.api.mimetype.IMimeType;
import org.pentaho.platform.api.repository2.unified.IPlatformImportBundle;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.importer.IPlatformImportHandler;
import org.pentaho.platform.plugin.services.importer.IPlatformImporter;
import org.pentaho.platform.plugin.services.importer.PlatformImportException;
import org.pentaho.platform.plugin.services.importer.RepositoryFileImportBundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicPluginImportHandlerWorker implements IPlatformImportHandler {

  private static final Log logger = LogFactory.getLog( BasicPluginImportHandlerWorker.class );

  private HashMap<String, IMimeType> mimeTypeMap = new HashMap<String, IMimeType>();

  public BasicPluginImportHandlerWorker( List<IMimeType> mimeTypes ) {

    for ( IMimeType mimeType : mimeTypes ) {
      this.mimeTypeMap.put( mimeType.getName(), mimeType );
    }

  }

  /**
   * Returns the mime type list this Import handler takes responsibility for
   */
  @Override
  public List<IMimeType> getMimeTypes() {
    return new ArrayList<IMimeType>( mimeTypeMap.values() );
  }


  @Override
  public void importFile( IPlatformImportBundle bundle ) throws PlatformImportException, DomainIdNullException,
      DomainAlreadyExistsException, DomainStorageException, IOException {

    logger.info( "importFile()" );

    try {

      IPlatformImporter importer = PentahoSystem.get( IPlatformImporter.class );

      // We'll just delegate this bundle to the registered 'text/plain' importer  ;)

      IPlatformImportHandler textPlainImportHandler = importer.getHandlers().get( "text/plain" );

      if( textPlainImportHandler != null ) {

        // let's change the mimeType to 'text/plain'
        textPlainImportHandler.importFile( buildNewBundle( bundle, "text/plain" ) );
      }

    } catch ( Exception e ) {
      throw new PlatformImportException( e.getMessage() );
    }
  }


  private IPlatformImportBundle buildNewBundle( IPlatformImportBundle bundle , String mimeType ) throws IOException {

    RepositoryFileImportBundle.Builder builderBundle = new RepositoryFileImportBundle.Builder();
    builderBundle.mime( mimeType );
    builderBundle.acl( bundle.getAcl() );
    builderBundle.charSet( bundle.getCharset() );
    builderBundle.input( bundle.getInputStream() );
    builderBundle.name( bundle.getName() );
    builderBundle.path( bundle.getPath() );
    builderBundle.preserveDsw( bundle.isPreserveDsw() );
    builderBundle.retainOwnership( bundle.isRetainOwnership() );
    builderBundle.overwriteAclSettings( bundle.isOverwriteAclSettings() );
    builderBundle.applyAclSettings( bundle.isApplyAclSettings() );
    builderBundle.hidden( false );

    return builderBundle.build();
  }
}
