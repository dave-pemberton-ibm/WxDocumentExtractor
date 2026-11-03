package wx;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class example

{
	// ---( internal utility methods )---

	final static example _instance = new example();

	static example _newInstance() { return new example(); }

	static example _cast(Object o) { return (example)o; }

	// ---( server methods )---




	public static final void HelloWorld (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(HelloWorld)>> ---
		// @sigtype java 3.5
		// [i] field:0:required name
		// [i] field:0:required age
		// [o] record:0:required results
		// [o] - field:0:required message
		// [o] - record:0:required details
		// [o] -- field:0:required name
		// [o] -- field:0:required age
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	name = IDataUtil.getString( pipelineCursor, "name" );
		String	age = IDataUtil.getString( pipelineCursor, "age" );
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		
		
		String message = "Hello " + name + ". Hope everything is great. ";
		message += "I see you are " + age + " years young!";
		
		// results
		IData	results = IDataFactory.create();
		IDataCursor resultsCursor = results.getCursor();
		IDataUtil.put( resultsCursor, "message", message);
		
		
		
		// results.details
		IData	details = IDataFactory.create();
		IDataCursor detailsCursor = details.getCursor();
		IDataUtil.put( detailsCursor, "name", name );
		IDataUtil.put( detailsCursor, "age", age );
		detailsCursor.destroy();
		IDataUtil.put( resultsCursor, "details", details );
		resultsCursor.destroy();
		IDataUtil.put( pipelineCursor_1, "results", results );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	public static final void test()
	{
		System.out.println("test");
	}
		
	// --- <<IS-END-SHARED>> ---
}

