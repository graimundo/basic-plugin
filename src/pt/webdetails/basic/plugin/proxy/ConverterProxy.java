package pt.webdetails.basic.plugin.proxy;

import org.pentaho.platform.api.repository2.unified.Converter;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import java.io.InputStream;
import java.io.Serializable;

public class ConverterProxy implements Converter {

  private Converter converter;

  public ConverterProxy( Converter converter ){
    this.converter = converter;
  }

  public ConverterProxy( String converterBeanId ){
    this.converter = PentahoSystem.get( Converter.class, converterBeanId, null );
  }

  @Override public IRepositoryFileData convert( InputStream inputStream, String charset, String mimeType ) {
    return converter.convert( inputStream, charset, mimeType );
  }

  @Override public InputStream convert( IRepositoryFileData data ) {
    return converter.convert( data );
  }

  @Override public InputStream convert( Serializable fileId ) {
    return converter.convert( fileId );
  }
}
