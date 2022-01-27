package wx.packages.manager._priv;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.attributes.AttributesNodeProvider;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.ReflogReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
// --- <<IS-END-IMPORTS>> ---

public final class git

{
	// ---( internal utility methods )---

	final static git _instance = new git();

	static git _newInstance() { return new git(); }

	static git _cast(Object o) { return (git)o; }

	// ---( server methods )---




	public static final void cloneGitRepo (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(cloneGitRepo)>> ---
		// @sigtype java 3.5
		// [i] field:0:required uri
		// [i] field:0:optional repoName
		// [i] field:0:optional tag
		// [i] field:0:optional user
		// [i] field:0:optional password
		// [i] field:0:required localDir
		// pipeline in
		
		IDataCursor cursor = pipeline.getCursor();
		String uri = IDataUtil.getString(cursor, "uri");
		String repo = IDataUtil.getString(cursor, "repoName");
		String tag = IDataUtil.getString(cursor, "tag");
		
		String user = IDataUtil.getString(cursor, "user");
		String password = IDataUtil.getString(cursor, "password");
		
		String localDirStr = IDataUtil.getString(cursor, "localDir");
		
		// process
		
		if (repo != null) {
			if (uri.endsWith("/"))
				uri += repo + ".git";
			else
				uri += "/" + repo + ".git";
		}
		
		File localDir = new File(localDirStr);
		
		if (!localDir.exists()) {
			localDir.mkdirs();
		} else if (!localDir.isDirectory()) {
			throw new ServiceException("BuildDir must be a directory: " + localDirStr);
		} else {
			
			System.out.println("Delete existing directory " + localDirStr);
		
			deleteDir(localDir);
			
			localDir.mkdir();
		}
				
		System.out.println("Cloning from " + uri);
		
		CloneCommand c = new CloneCommand();
		c.setURI(uri);
		c.setCloneSubmodules(true);
		
		if (tag != null) {
			c.setBranch(tag);
		}
		
		
		if (user != null && password != null)
			c.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password));
		
		c.setDirectory(localDir);
		
		try {
			c.call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
			throw new ServiceException("Invalid GIT source: " + e.getMessage());
		} catch (TransportException e) {
			e.printStackTrace();
			throw new ServiceException("Got an exception connecting to GIT repo: " + e.getMessage());
		} catch (JGitInternalException e) {
			e.printStackTrace();
			throw new ServiceException("Got an internal exception from GIT API: This might be caused by a badly referenced submodule, " + e.getMessage());
		} catch (GitAPIException e) {
			e.printStackTrace();
			throw new ServiceException("Got an exception from GIT API: " + e.getMessage());
		}
		
		try {
			Git git = Git.open(localDir);
			
			TagCommand t = git.tag();
			
			
			if (t.isSigned()) {
				System.out.println("tag " + tag + " is signed");
				
				System.out.println("name is " + t.getName());
				System.out.println("key is " + t.getSigningKey());
				
				if (t.getObjectId() != null)
					System.out.println("object name is " + t.getObjectId().getName());
			} else {
				System.out.println("tag " + tag + " is not signed");
				
				System.out.println("name is " + t.getName());
				System.out.println("key is " + t.getSigningKey());
				
				if (t.getObjectId() != null)
					System.out.println("object name is " + t.getObjectId().getName());
			}
			
			Repository r = git.getRepository();
			
			StoredConfig config = r.getConfig();
			
			config.getSections().forEach(s -> {
				System.out.println("section " + s);
		
				config.getNames(s).forEach(n -> {
					System.out.println("name " + n);
				});
			});
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// pipeline out
		
		cursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void tags (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(tags)>> ---
		// @sigtype java 3.5
		// [i] field:0:required gitUrl
		// [i] field:0:optional gitUser
		// [i] field:0:optional gitPassword
		// [i] field:0:optional tag
		// [o] field:1:required tags
		IDataCursor c = pipeline.getCursor();
		String url = IDataUtil.getString(c, "gitUrl");
		String user = IDataUtil.getString(c, "gitUser");
		String password = IDataUtil.getString(c, "gitPassword");
		
		String tag = IDataUtil.getString(c, "tag");
		
		// process
		
		ArrayList<String> tags = new ArrayList<String>();
		
		try {
			Collection<Ref> map;
			
			if (user != null && !user.equals("null")) {
				
				System.out.println("will use credentials for " + user);
				
				map = Git.lsRemoteRepository()
			        .setRemote(url)
			        .setTags(true)
			        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password))
			        .call();
			} else {
				map = Git.lsRemoteRepository()
					.setRemote(url)
				    .setTags(true)
				    .call();
			}
		    
			for (Ref entry : map) {				
				
				ObjectId peeledRef = entry.getPeeledObjectId();
				
				if (tag != null) {
					
					System.out.println("comparing '" + entry.getName().substring(10)+ "' == '" + tag + "'");
					
					if (entry.getName().substring(10).equals(tag)) {
						if (peeledRef != null) {
							tags.add(peeledRef.getName());
						} else {
							tags.add(entry.getObjectId().getName());
						}
					}
				} else {
					tags.add(entry.getName().substring(10));
				}
		    }
		} catch (GitAPIException e) {
			throw new ServiceException(e);
		}
			
		// pipeline out
		
		IDataUtil.put(c, "tags", tags.toArray(new String[tags.size()]));
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            if (! Files.isSymbolicLink(f.toPath())) {
	                deleteDir(f);
	            }
	        }
	    }
	    file.delete();
	}
	// --- <<IS-END-SHARED>> ---
}

