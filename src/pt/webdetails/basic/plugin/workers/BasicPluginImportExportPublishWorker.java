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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.mimetype.IMimeType;
import org.pentaho.platform.api.mimetype.IPlatformMimeResolver;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.importer.IPlatformImportHandler;
import org.pentaho.platform.plugin.services.importer.IPlatformImporter;
import org.pentaho.platform.plugin.services.importexport.DefaultExportHandler;
import org.pentaho.platform.plugin.services.importexport.ExportHandler;

import java.util.List;

public class BasicPluginImportExportPublishWorker implements IBasicPluginWorker {

  private static final Log logger = LogFactory.getLog( BasicPluginImportExportPublishWorker.class );

  private IPlatformImporter platformImporter;
  private ExportHandler platformExportHandler;
  private IPlatformMimeResolver platformMimeResolver;

  private List<IPlatformImportHandler> importHandlers;
  private List<String> localeExportList;
  private List<IMimeType> mimeTypeResolvers;

  public BasicPluginImportExportPublishWorker() {
    this( PentahoSystem.get( IPlatformImporter.class ),
          PentahoSystem.get( DefaultExportHandler.class ),
          PentahoSystem.get( IPlatformMimeResolver.class ) );
  }

  public BasicPluginImportExportPublishWorker( IPlatformImporter importer, ExportHandler exportHandler,
      IPlatformMimeResolver mimeResolver ) {

    if( importer != null ) {
      setPlatformImporter( importer );
    } else {
      logger.warn( "No IPlatformImporter provided; IPlatformImportHandler adding will not be possible" );
    }

    if( exportHandler != null ) {
      setPlatformExportHandler( exportHandler );
    } else {
      logger.warn( "No ExportHandler provided; locale export adding will not be possible" );
    }

    if( mimeResolver != null ){
      setPlatformMimeResolver( mimeResolver );
    } else {
      logger.warn( "No IPlatformMimeResolver provided; IMimeType adding will not be possible" );
    }
  }

  @Override public void work() {

    // file import handlers
    if( getPlatformImporter() != null && getImportHandlers() != null ){

      for( IPlatformImportHandler handler : getImportHandlers() ){
        getPlatformImporter().addHandler( handler );
        logger.info( "Added '" + handler.getClass().getSimpleName() + "' to " + getPlatformImporter().getClass().getSimpleName() );
      }
    }

    // export handler
    if( getPlatformExportHandler() != null && getLocaleExportList() != null ){

      // there's only one registered ExportHandler, which is not published as-type INTERFACES
      if( getPlatformExportHandler() instanceof DefaultExportHandler ){

        if( ( ( DefaultExportHandler ) getPlatformExportHandler() ).getLocaleExportList() == null ){
          ( ( DefaultExportHandler ) getPlatformExportHandler() ).setLocaleExportList( getLocaleExportList() );
        } else {
          ( ( DefaultExportHandler ) getPlatformExportHandler() ).getLocaleExportList().addAll( getLocaleExportList() );
        }
        logger.info( "Added '" + StringUtils.join( getLocaleExportList(), "," ) + "' to "
            + getPlatformExportHandler().getClass().getSimpleName() + "'s localeExportList");

      } else {
        logger.info( "Unknown exportHandler '" + getPlatformExportHandler().getClass() + "' class type" );
      }
    }

    // mimeType resolvers
    if( getPlatformMimeResolver() != null && getMimeTypeResolvers() != null ) {

      for( IMimeType mime : getMimeTypeResolvers() ) {
        getPlatformMimeResolver().addMimeType( mime );
        logger.info( "Added '" + mime.getName() + "' to " + getPlatformMimeResolver().getClass().getSimpleName() );
      }
    }
  }

  public IPlatformImporter getPlatformImporter() {
    return platformImporter;
  }

  public void setPlatformImporter( IPlatformImporter platformImporter ) {
    this.platformImporter = platformImporter;
  }

  public IPlatformMimeResolver getPlatformMimeResolver() {
    return platformMimeResolver;
  }

  public void setPlatformMimeResolver( IPlatformMimeResolver platformMimeResolver ) {
    this.platformMimeResolver = platformMimeResolver;
  }

  public List<IPlatformImportHandler> getImportHandlers() {
    return importHandlers;
  }

  public void setImportHandlers( List<IPlatformImportHandler> importHandlers ) {
    this.importHandlers = importHandlers;
  }

  public ExportHandler getPlatformExportHandler() {
    return platformExportHandler;
  }

  public void setPlatformExportHandler( ExportHandler platformExportHandler ) {
    this.platformExportHandler = platformExportHandler;
  }

  public List<IMimeType> getMimeTypeResolvers() {
    return mimeTypeResolvers;
  }

  public void setMimeTypeResolvers( List<IMimeType> mimeTypeResolvers ) {
    this.mimeTypeResolvers = mimeTypeResolvers;
  }

  public List<String> getLocaleExportList() {
    return localeExportList;
  }

  public void setLocaleExportList( List<String> localeExportList ) {
    this.localeExportList = localeExportList;
  }
}
