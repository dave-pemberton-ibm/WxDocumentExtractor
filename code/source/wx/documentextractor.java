package wx;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.xml.sax.SAXException;
// --- <<IS-END-IMPORTS>> ---

public final class documentextractor

{
	// ---( internal utility methods )---

	final static documentextractor _instance = new documentextractor();

	static documentextractor _newInstance() { return new documentextractor(); }

	static documentextractor _cast(Object o) { return (documentextractor)o; }

	// ---( server methods )---




	public static final void detectType (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(detectType)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional content
		// [i] field:0:optional filePath
		// [i] field:0:optional fileName
		// [o] field:0:required contentType
		_detectType(pipeline);
		// --- <<IS-END>> ---

                
	}



	public static final void extractRecursive (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(extractRecursive)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional content
		// [i] field:0:optional filePath
		// [i] field:0:optional fileName
		// [i] field:0:optional password
		// [i] field:0:optional maxChars
		// [o] field:0:required documentCount
		// [o] record:1:required documents
		// [o] - field:0:required embeddedPath
		// [o] - field:0:required depth
		// [o] - field:0:required contentType
		// [o] - field:0:required text
		// [o] - field:0:required truncated
		// [o] - field:0:optional error
		// [o] - record:0:required metadata
		_extractRecursive(pipeline);
		// --- <<IS-END>> ---

                
	}



	public static final void extractText (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(extractText)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional content
		// [i] field:0:optional filePath
		// [i] field:0:optional fileName
		// [i] field:0:optional password
		// [i] field:0:optional maxChars
		// [o] field:0:required text
		// [o] field:0:required contentType
		// [o] field:0:required truncated
		_extractText(pipeline, false);
		// --- <<IS-END>> ---

                
	}



	public static final void extractWithMetadata (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(extractWithMetadata)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional content
		// [i] field:0:optional filePath
		// [i] field:0:optional fileName
		// [i] field:0:optional password
		// [i] field:0:optional maxChars
		// [o] field:0:required text
		// [o] field:0:required contentType
		// [o] field:0:required truncated
		// [o] record:0:required metadata
		_extractText(pipeline, true);
		// --- <<IS-END>> ---

                
	}



	public static final void getSupportedTypes (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSupportedTypes)>> ---
		// @sigtype java 3.5
		// [o] field:0:required tikaVersion
		// [o] field:0:required tikaCoreLocation
		// [o] field:1:required types
		// [o] field:0:required typeCount
		_getSupportedTypes(pipeline);
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	/** Default per-document character limit when maxChars is not supplied. -1 means unlimited. */
	private static final int _DEFAULT_MAX_CHARS = 1000000;

	private static volatile AutoDetectParser _parser;

	/** Parsed service inputs shared by all services. */
	private static final class _Input
	{
		TikaInputStream stream;
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		int maxChars = _DEFAULT_MAX_CHARS; 
	}

	/**
	 * Tika discovers parsers via ServiceLoader on the thread context class loader. Inside IS the
	 * context class loader is not guaranteed to be this package's loader, so set it for the
	 * duration of every call.
	 */
	private static ClassLoader _enter()
	{
		Thread t = Thread.currentThread();
		ClassLoader old = t.getContextClassLoader();
		t.setContextClassLoader(documentextractor.class.getClassLoader());
		return old;
	}

	private static void _exit(ClassLoader old)
	{
		Thread.currentThread().setContextClassLoader(old);
	}

	/**
	 * Lazily creates the (thread safe) Tika parser. Must be called between _enter() and _exit().
	 * Tika's ServiceLoader ignores the thread context class loader (it uses the loader of tika-core,
	 * which in IS is the server loader and cannot see this package's parser jars), so the package
	 * class loader is passed to TikaConfig explicitly.
	 */
	private static AutoDetectParser _getParser() throws IOException, TikaException
	{
		AutoDetectParser p = _parser;
		if (p == null)
		{
			synchronized (documentextractor.class)
			{
				p = _parser;
				if (p == null)
				{
					p = new AutoDetectParser(new TikaConfig(documentextractor.class.getClassLoader()));
					_parser = p;
				}
			}
		}
		return p;
	}

	/** Reads content / filePath / fileName / password / maxChars from the pipeline. */
	private static _Input _open(IData pipeline) throws ServiceException
	{
		_Input in = new _Input();
		Object content;
		String filePath, fileName, password, maxChars;

		IDataCursor c = pipeline.getCursor();
		try
		{
			content = IDataUtil.get(c, "content");
			filePath = IDataUtil.getString(c, "filePath");
			fileName = IDataUtil.getString(c, "fileName");
			password = IDataUtil.getString(c, "password");
			maxChars = IDataUtil.getString(c, "maxChars");
		}
		finally
		{
			c.destroy();
		}

		if (fileName != null && fileName.length() > 0)
			in.metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);

		if (maxChars != null && maxChars.trim().length() > 0)
		{
			try
			{
				in.maxChars = Integer.parseInt(maxChars.trim());
			}
			catch (NumberFormatException e)
			{
				throw new ServiceException("maxChars must be an integer (-1 for unlimited): " + maxChars);
			}
		}

		if (password != null && password.length() > 0)
		{
			final String pw = password;
			in.context.set(PasswordProvider.class, new PasswordProvider()
			{
				public String getPassword(Metadata m)
				{
					return pw;
				}
			});
		}

		try
		{
			if (content instanceof byte[])
				in.stream = TikaInputStream.get((byte[]) content, in.metadata);
			else if (content instanceof InputStream)
				in.stream = TikaInputStream.get((InputStream) content);
			else if (content != null)
				throw new ServiceException("content must be byte[] or java.io.InputStream but was "
						+ content.getClass().getName());
			else if (filePath != null && filePath.trim().length() > 0)
			{
				java.io.File f = new java.io.File(filePath.trim());
				if (!f.exists())
					throw new ServiceException("filePath does not exist on the Integration Server host: " + f);
				if (f.isDirectory())
					throw new ServiceException("filePath is a directory, not a file: " + f);
				if (!f.canRead())
					throw new ServiceException("filePath is not readable by the Integration Server process: " + f);
				in.stream = TikaInputStream.get(Paths.get(f.getPath()), in.metadata);
			}
			else
				throw new ServiceException("Either content or filePath must be provided");
		}
		catch (IOException e)
		{
			throw new ServiceException(e);
		}
		return in;
	}

	private static void _close(_Input in)
	{
		if (in != null && in.stream != null)
		{
			try
			{
				in.stream.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}

	/** Converts Tika metadata to an IData; multi-valued properties become String[]. */
	private static IData _metadataToIData(Metadata md)
	{
		IData d = IDataFactory.create();
		IDataCursor c = d.getCursor();
		String[] names = md.names();
		Arrays.sort(names);
		for (String name : names)
		{
			if (name.equals(TikaCoreProperties.TIKA_CONTENT.getName()))
				continue;
			String[] v = md.getValues(name);
			if (v.length == 1)
				IDataUtil.put(c, name, v[0]);
			else
				IDataUtil.put(c, name, v);
		}
		c.destroy();
		return d;
	}

	private static void _extractText(IData pipeline, boolean includeMetadata) throws ServiceException
	{
		ClassLoader cl = _enter();
		_Input in = null;
		try
		{
			in = _open(pipeline);
			BodyContentHandler handler = new BodyContentHandler(in.maxChars);
			boolean truncated = false;
			try
			{
				_getParser().parse(in.stream, handler, in.metadata, in.context);
			}
			catch (SAXException e)
			{
				if (WriteLimitReachedException.isWriteLimitReached(e))
					truncated = true;
				else
					throw e;
			}

			IDataCursor out = pipeline.getCursor();
			IDataUtil.put(out, "text", handler.toString());
			IDataUtil.put(out, "contentType", in.metadata.get("Content-Type"));
			IDataUtil.put(out, "truncated", String.valueOf(truncated));
			if (includeMetadata)
				IDataUtil.put(out, "metadata", _metadataToIData(in.metadata));
			out.destroy();
		}
		catch (IOException | SAXException | TikaException e)
		{
			throw new ServiceException(e);
		}
		finally
		{
			_close(in);
			_exit(cl);
		}
	}

	private static void _extractRecursive(IData pipeline) throws ServiceException
	{
		ClassLoader cl = _enter();
		_Input in = null;
		try
		{
			in = _open(pipeline);
			RecursiveParserWrapper wrapper = new RecursiveParserWrapper(_getParser());
			RecursiveParserWrapperHandler handler = new RecursiveParserWrapperHandler(
					new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.TEXT, in.maxChars));
			wrapper.parse(in.stream, handler, in.metadata, in.context);

			List<Metadata> list = handler.getMetadataList();
			IData[] docs = new IData[list.size()];
			for (int i = 0; i < docs.length; i++)
			{
				Metadata md = list.get(i);
				String text = md.get(TikaCoreProperties.TIKA_CONTENT);
				String path = md.get(TikaCoreProperties.EMBEDDED_RESOURCE_PATH);
				String depth = md.get(TikaCoreProperties.EMBEDDED_DEPTH);
				String error = md.get(TikaCoreProperties.CONTAINER_EXCEPTION);
				if (error == null)
					error = md.get(TikaCoreProperties.EMBEDDED_EXCEPTION);

				IData doc = IDataFactory.create();
				IDataCursor dc = doc.getCursor();
				IDataUtil.put(dc, "embeddedPath", path == null ? "/" : path);
				IDataUtil.put(dc, "depth", depth == null ? "0" : depth);
				IDataUtil.put(dc, "contentType", md.get("Content-Type"));
				IDataUtil.put(dc, "text", text == null ? "" : text);
				IDataUtil.put(dc, "truncated",
						String.valueOf("true".equals(md.get(TikaCoreProperties.WRITE_LIMIT_REACHED))));
				if (error != null)
					IDataUtil.put(dc, "error", error);
				IDataUtil.put(dc, "metadata", _metadataToIData(md));
				dc.destroy();
				docs[i] = doc;
			}

			IDataCursor out = pipeline.getCursor();
			IDataUtil.put(out, "documentCount", String.valueOf(docs.length));
			IDataUtil.put(out, "documents", docs);
			out.destroy();
		}
		catch (IOException | SAXException | TikaException e)
		{
			throw new ServiceException(e);
		}
		finally
		{
			_close(in);
			_exit(cl);
		}
	}

	private static void _detectType(IData pipeline) throws ServiceException
	{
		ClassLoader cl = _enter();
		_Input in = null;
		try
		{
			in = _open(pipeline);
			MediaType type = _getParser().getDetector().detect(in.stream, in.metadata);
			IDataCursor out = pipeline.getCursor();
			IDataUtil.put(out, "contentType", type.toString());
			out.destroy();
		}
		catch (IOException | TikaException e)
		{
			throw new ServiceException(e);
		}
		finally
		{
			_close(in);
			_exit(cl);
		}
	}

	private static void _getSupportedTypes(IData pipeline) throws ServiceException
	{
		ClassLoader cl = _enter();
		try
		{
			Set<String> sorted = new TreeSet<String>();
			for (MediaType t : _getParser().getSupportedTypes(new ParseContext()))
				sorted.add(t.toString());

			String version = Metadata.class.getPackage().getImplementationVersion();
			java.security.CodeSource cs = Metadata.class.getProtectionDomain().getCodeSource();

			IDataCursor out = pipeline.getCursor();
			IDataUtil.put(out, "tikaVersion", version == null ? "unknown" : version);
			IDataUtil.put(out, "tikaCoreLocation", cs == null ? "unknown" : cs.getLocation().toString());
			IDataUtil.put(out, "types", sorted.toArray(new String[0]));
			IDataUtil.put(out, "typeCount", String.valueOf(sorted.size()));
			out.destroy();
		}
		catch (IOException | TikaException e)
		{
			throw new ServiceException(e);
		}
		finally
		{
			_exit(cl);
		}
	}
	
	
	
	// --- <<IS-END-SHARED>> ---
}

