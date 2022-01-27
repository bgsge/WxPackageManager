package wx.packages.manager._priv;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.HashMap;
import com.softwareag.is.dynamicvariables.DynamicVariablesEncryptor;
// --- <<IS-END-IMPORTS>> ---

public final class tools

{
	// ---( internal utility methods )---

	final static tools _instance = new tools();

	static tools _newInstance() { return new tools(); }

	static tools _cast(Object o) { return (tools)o; }

	// ---( server methods )---




	public static final void countPackage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(countPackage)>> ---
		// @sigtype java 3.5
		// [i] field:0:required registry
		// [i] field:0:required packageName
		// [o] field:0:required newCount
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String registry = IDataUtil.getString(pipelineCursor, "registry");
		String packageName = IDataUtil.getString(pipelineCursor, "packageName");
		
		// process
		
		String key = (registry != null ? registry : "default") + "-" + packageName;
		int newCount = 1;
		
		synchronized (_packageCounter) {
			if (_packageCounter.get(key) == null) {
				_packageCounter.put(key, 1);
			} else {
				newCount = _packageCounter.get(key) + 1;
				_packageCounter.put(key,  newCount);
			}
		}
		
		 
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "newCount", "" + newCount);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void decrypt (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(decrypt)>> ---
		// @sigtype java 3.5
		// [i] field:0:required encryptedText
		// [o] field:0:required text
		IDataCursor c = pipeline.getCursor();
		String data = IDataUtil.getString(c, "encryptedText");
		
		// process
		
		String text = null;
		try {
			text = DynamicVariablesEncryptor.instance().decryptData(data);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		// pipeline out
		
		if (text != null)
			IDataUtil.put(c, "text", text);
			
		// --- <<IS-END>> ---

                
	}



	public static final void defaultRegistry (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(defaultRegistry)>> ---
		// @sigtype java 3.5
		// [o] field:0:required registry
		
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "registry", _defaultRegistry);
		c.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getTaskId (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getTaskId)>> ---
		// @sigtype java 3.5
		// [o] field:0:required taskId
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "taskId", _taskId);
		c.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void isInList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isInList)>> ---
		// @sigtype java 3.5
		// [i] record:1:required trustedTags
		// [i] - field:0:required tag
		// [i] field:0:required tag
		// [o] object:0:required exists
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String tag = IDataUtil.getString(pipelineCursor, "tag");
		IData[]	trustedTags = IDataUtil.getIDataArray(pipelineCursor, "trustedTags");
		
		// process
		
		boolean found = false;
		
		if (trustedTags != null) {
			
			for (int i = 0; i < trustedTags.length; i++) {
				
				if (trustedTags[i] != null) {
					IDataCursor trustedTagsCursor = trustedTags[i].getCursor();
					String t = IDataUtil.getString(trustedTagsCursor, "tag");
					trustedTagsCursor.destroy();
				
					if (t != null && tag.equals(t)) {
						found = true;
						break;
					}
				}
			}
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "exists", found);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void packageCount (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(packageCount)>> ---
		// @sigtype java 3.5
		// [i] field:0:required registry
		// [i] field:0:required packageName
		// [o] field:0:required count
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String registry = IDataUtil.getString(pipelineCursor, "registry");
		String packageName = IDataUtil.getString(pipelineCursor, "packageName");
		
		// process
		
		String key = (registry != null ? registry : "default") + "-" + packageName;
		int count = 0;
		
		if (_packageCounter.get(key) != null) {
			count = _packageCounter.get(key);
		}
		 
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "count", "" + count);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void packageCounts (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(packageCounts)>> ---
		// @sigtype java 3.5
		// [o] record:1:required counts
		// [o] - field:0:required registry
		// [o] - field:0:required packageName
		// [o] - object:0:required count
		ArrayList<IData> counts = new ArrayList<IData>();
		
		for (String key : _packageCounter.keySet()) {
			String registry = key.substring(0, key.indexOf("-"));
			String packageName = key.substring(key.indexOf("-"));
			
			IData count = IDataFactory.create();
			IDataCursor cc = count.getCursor();
			IDataUtil.put(cc, "registry", registry);
			IDataUtil.put(cc,  "packageName", packageName);
			IDataUtil.put(cc,  "count", _packageCounter.get(key));
			cc.destroy();
			
			counts.add(count);		
		}
		
		_packageCounter.clear();
		
		// pipeline out
		
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "counts", counts);
		c.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void recordTaskId (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(recordTaskId)>> ---
		// @sigtype java 3.5
		// [i] field:0:required taskId
		IDataCursor c = pipeline.getCursor();
		_taskId = IDataUtil.getString(c, "taskId");
		c.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void setDefaultRegistry (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setDefaultRegistry)>> ---
		// @sigtype java 3.5
		// [i] field:0:required registry
		IDataCursor c = pipeline.getCursor();
		String defaultRegistry = IDataUtil.getString(c, "registry");
		c.destroy();
		
		if (defaultRegistry != null && defaultRegistry.length() > 0) {
			_defaultRegistry = defaultRegistry;
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void splitUrl (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(splitUrl)>> ---
		// @sigtype java 3.5
		// [i] field:0:required url
		// [o] field:0:required owner
		// [o] field:0:required repo
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String url = IDataUtil.getString(pipelineCursor, "url" );
		pipelineCursor.destroy();
		
		// process
		
		String owner = null;
		String repo = null;
		
		if (url != null) {
		
			// https://github.com/SoftwareAG/wm-is-client.git
			
			repo = url.substring(url.lastIndexOf("/")+1, url.length() - 4);
			owner = url.substring(0, url.length() - (repo.length() + 5));
			owner = owner.substring(owner.lastIndexOf("/")+1);
			
		}
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "owner", owner);
		IDataUtil.put(pipelineCursor, "repo", repo);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static String _taskId = null;
	
	private static String _defaultRegistry = "default";
	
	private static HashMap<String, Integer> _packageCounter = new HashMap<String, Integer>();
	// --- <<IS-END-SHARED>> ---
}

