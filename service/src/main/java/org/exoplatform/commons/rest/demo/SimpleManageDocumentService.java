/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.commons.rest.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

//import javax.annotation.security.RolesAllowed;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
//import org.exoplatform.ecm.connector.fckeditor.FCKUtils;
//import org.exoplatform.ecm.utils.text.Text;
//import org.exoplatform.services.cms.drives.DriveData;
//import org.exoplatform.services.cms.drives.ManageDriveService;
//import org.exoplatform.services.cms.impl.Utils;
//import org.exoplatform.services.cms.link.LinkManager;
//import org.exoplatform.services.context.DocumentContext;
//import org.exoplatform.services.jcr.RepositoryService;
//import org.exoplatform.services.jcr.access.PermissionType;
//import org.exoplatform.services.jcr.core.ManageableRepository;
//import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.util.Text;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.MembershipEntry;
//import org.exoplatform.services.wcm.core.NodetypeConstant;
//import org.exoplatform.services.wcm.utils.WCMCoreUtils;
//import org.exoplatform.wcm.connector.FileUploadHandler;

import org.exoplatform.services.rest.resource.ResourceContainer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is a service which is used to perform some actions on a folder or on a file, such as creating, or deleting a folder/file, or uploading a file.
 *
 * @author Lai Trung Hieu <hieu.lai@exoplatform.com>
 * @since      6 Apr 2011
 * @copyright  eXo Platform SEA
 * 
 * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService
 */
@Path("/managedocument/")
public class SimpleManageDocumentService extends AbstractManageDocumentService {

  /** The Constant IF_MODIFIED_SINCE_DATE_FORMAT. */
  protected static final String IF_MODIFIED_SINCE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

  /** The Constant LAST_MODIFIED_PROPERTY. */
  protected static final String LAST_MODIFIED_PROPERTY = "Last-Modified";

  /** The cache control. */
  private final CacheControl    cc;

  /** The log. */
  private static final Log LOG = ExoLogger.getLogger(SimpleManageDocumentService.class.getName());

  //private ManageDriveService    manageDriveService;

  //private LinkManager linkManager;  

  /** The file upload handler. */
  //protected FileUploadHandler   fileUploadHandler;
  
  private RepositoryService repositoryService;

  private enum DriveType {
    GENERAL, GROUP, PERSONAL
  }

  final static public String   EXO_MUSICFOLDER      = "exo:musicFolder";

  final static public String   EXO_VIDEOFOLDER      = "exo:videoFolder";

  final static public String   EXO_PICTUREFOLDER    = "exo:pictureFolder";

  final static public String   EXO_DOCUMENTFOLDER   = "exo:documentFolder";

  final static public String   EXO_SEARCHFOLDER     = "exo:searchFolder";

  final static public String   EXO_SYMLINK          = "exo:symlink";

  final static public String   EXO_PRIMARYTYPE      = "exo:primaryType";

  final static public String   EXO_TRASH_FOLDER     = "exo:trashFolder";

  final static public String   EXO_FAVOURITE_FOLDER = "exo:favoriteFolder";

  final static public String   NT_UNSTRUCTURED      = "nt:unstructured";

  final static public String   NT_FOLDER            = "nt:folder";
  
  final static public String   NT_FILE            = "nt:file";

  final static public String[] SPECIFIC_FOLDERS = { EXO_MUSICFOLDER,
    EXO_VIDEOFOLDER, EXO_PICTUREFOLDER, EXO_DOCUMENTFOLDER, EXO_SEARCHFOLDER };

  private static final String  PRIVATE              = "Private";
  /**
   * Instantiates a new platform document selector.
   *
   * @param manageDriveService
   */
  public SimpleManageDocumentService() {
    //this.manageDriveService = manageDriveService;
    //this.linkManager = linkManager;
    //fileUploadHandler = new FileUploadHandler();
    
    PortalContainer container = PortalContainer.getInstance();
    this.repositoryService = (RepositoryService) container.getComponentInstanceOfType(RepositoryService.class);
    
    cc = new CacheControl();
    cc.setNoCache(true);
    cc.setNoStore(true);
  }

  @GET
  @Path("/getLocals/")
  //@RolesAllowed("users")
  public Response getLocals(@QueryParam("repo") String repositoryName) throws Exception {
    Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    document = builder.newDocument();

    Element rootElement = document.createElement("Locals");
    document.appendChild(rootElement);
    
    List<String> locals = new ArrayList<String>();
    
    String currentRepo = repositoryService.getCurrentRepository().getConfiguration().getName();
    String defaultRepo = repositoryService.getDefaultRepository().getConfiguration().getName();
    
    /*
    locals.add(currentRepo);
    if (!defaultRepo.equals(currentRepo))
      locals.add(defaultRepo);
    
    rootElement.appendChild(buildXMLLocalSelections(document, locals));
    */
    
    locals = getWorkspaces(currentRepo);
    rootElement.appendChild(buildXMLLocalSelections(document, locals));
    
    return Response.ok(new DOMSource(document), MediaType.TEXT_XML).cacheControl(cc).build();
  }
                            
  /**
   * Get all drives by type (General, Group or Personal). 
   * 
   * @param driveType The types of drive (General, Group, or Personal).
   * @param showPrivate Show the Private drive or not. The default value is false.
   * @param showPersonal Show the Personal drive or not. The default value is false.
   * @return {@link Document} Contain the drives.
   * 
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.getDrives
   */
  @GET
  @Path("/getDrives/")
  //@RolesAllowed("users")
  public Response getDrives(@QueryParam("driveType") String workspaceName,
                            @DefaultValue("false") @QueryParam("showPrivate") String showPrivate,
                            @DefaultValue("false") @QueryParam("showPersonal") String showPersonal) throws Exception {
              
//    Session session = repositoryService.getCurrentRepository().getSystemSession(workspaceName);
//    Node node = session.getRootNode();
//    return buildXMLResponseForChildren(node, "", "", Boolean.valueOf(true)); 
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    List<String> userRoles = getMemberships();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();

    Element rootElement = document.createElement("Folders");
    document.appendChild(rootElement);
    String userId = ConversationState.getCurrent().getIdentity().getUserId();
    
    List<String> locals = new ArrayList<String>();
    
    String currentRepo = repositoryService.getCurrentRepository().getConfiguration().getName();
    String defaultRepo = repositoryService.getDefaultRepository().getConfiguration().getName();
    
    /*
    locals.add(currentRepo);
    if (!defaultRepo.equals(currentRepo))
      locals.add(defaultRepo);
    
    rootElement.appendChild(buildXMLLocalSelections(document, locals));
    */
    
    locals = getWorkspaces(currentRepo);
    rootElement.appendChild(buildXMLDriveNodes(document, locals, currentRepo));
        
    return Response.ok(new DOMSource(document), MediaType.TEXT_XML).cacheControl(cc).build();
  }

//  @GET
//  @Path("/getWorkspaces/")
//  public String[] getWorkspaces(@QueryParam("repo") String repositoryName){
  private List<String> getWorkspaces(String repositoryName) throws RepositoryException, RepositoryConfigurationException{
    
    ManageableRepository repo;
    if (repositoryName == null)                
      repo = repositoryService.getCurrentRepository();          
    else 
      repo = repositoryService.getRepository(repositoryName);
      
    return Arrays.asList(repo.getWorkspaceNames());         
  }
  
  /**
   * Get all folders and files which can be viewed by the current user.
   *
   * @param driveName The name of drive.
   * @param workspaceName The name of workspace.
   * @param currentFolder The path to the folder to achieve its folders and files.
   * @param showHidden Show the hidden items or not. The default value is false.
   *
   * @return {@link Document} Contain the folders and files.
   *
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.getFoldersAndFiles
   */
  @GET
  @Path("/getFoldersAndFiles/")
  //@RolesAllowed("users")
  public Response getFoldersAndFiles(@QueryParam("driveName") String driveName,
                                     @QueryParam("workspaceName") String workspaceName,
                                     @QueryParam("currentFolder") String currentFolder,
                                     @DefaultValue("false") @QueryParam("showHidden") String showHidden) {
    try {
      Node node = getNode(driveName, workspaceName, currentFolder);
      return buildXMLResponseForChildren(node, driveName, currentFolder, Boolean.valueOf(showHidden));
    } catch (AccessDeniedException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Access is denied when perform get Folders and files: ", e);
      }
      return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).cacheControl(cc).build();
    }
    catch (PathNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Item is not found: ", e);
      }
      return Response.status(Status.NOT_FOUND).entity(e.getMessage()).cacheControl(cc).build();
    } catch (RepositoryException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Repository is error: ", e);
      }
      return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).cacheControl(cc).build();

    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error when perform get Folders and files: ", e);
      }
      return Response.serverError().entity(e.getMessage()).cacheControl(cc).build();
    }
  }

  /**
   * Delete a folder/file.
   * 
   * @param driveName The name of drive.
   * @param workspaceName The name of workspace.
   * @param itemPath The path to the folder/file.
   *
   * @return {@link Response} Return the status of an item which has been deleted.
   *
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.deleteFolderOrFile
   */
  @GET
  @Path("/deleteFolderOrFile/")
  //@RolesAllowed("users")
  public Response deleteFolderOrFile(@QueryParam("driveName") String driveName,
                                     @QueryParam("workspaceName") String workspaceName,
                                     @QueryParam("itemPath") String itemPath){
    try {
      Node node = getNode(driveName, workspaceName, itemPath);
      Node parent = node.getParent();
      node.remove();
      parent.save();
      return Response.ok().cacheControl(cc).build();
    } catch (AccessDeniedException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Access is denied when perform delete folder or file: ", e);
      }
      return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).cacheControl(cc).build();
    }
    catch (PathNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Item is not found: ", e);
      }
      return Response.status(Status.NOT_FOUND).entity(e.getMessage()).cacheControl(cc).build();
    } catch (RepositoryException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Repository is error: ", e);
      }
      return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).cacheControl(cc).build();

    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error when perform delete Folder or file: ", e);
      }
      return Response.serverError().entity(e.getMessage()).cacheControl(cc).build();
    }
  }

  /**
   * Create a new folder and return its information.
   *  
   * @param driveName The name of drive.
   * @param workspaceName The name of workspace.
   * @param currentFolder The path to the folder where a child folder is added.
   * @param folderName The name of folder.
   *
   * @return {@link Document} Contain the created folder.
   *
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.createFolder
   */
  @GET
  @Path("/createFolder/")
  //@RolesAllowed("users")
  public Response createFolder(@QueryParam("driveName") String driveName,
                               @QueryParam("workspaceName") String workspaceName,
                               @QueryParam("currentFolder") String currentFolder,
                               @QueryParam("folderName") String folderName) throws Exception {
    try {
      Node node = getNode(driveName, workspaceName, currentFolder);
      Node newNode = node.addNode(folderName, NT_UNSTRUCTURED);
      node.save();
      Document document = createNewDocument();
      String childFolder = StringUtils.isEmpty(currentFolder) ? newNode.getName() : currentFolder.concat("/")
                                                                                                 .concat(newNode.getName());
      Element folderNode = createFolderElement(document, newNode, workspaceName, driveName, childFolder);
      document.appendChild(folderNode);
      return getResponse(document);
    } catch (AccessDeniedException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Access is denied when perform create folder: ", e);
      }
      return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).cacheControl(cc).build();
    } catch (PathNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Item is not found: ", e);
      }
      return Response.status(Status.NOT_FOUND).entity(e.getMessage()).cacheControl(cc).build();
    } catch (RepositoryException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Repository is error: ", e);
      }
      return Response.status(Status.SERVICE_UNAVAILABLE)
                     .entity(e.getMessage())
                     .cacheControl(cc)
                     .build();

    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Error when perform create folder: ", e);
      }
      return Response.serverError().entity(e.getMessage()).cacheControl(cc).build();
    }
  }

  /**
   * Upload a file to the server.
   *
   * @param uploadId The Id of uploaded resource.
   * @param servletRequest The servlet request.
   *
   * @return The response.
   *
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.uploadFile
   */
  @POST
  @Path("/uploadFile/upload/")
//  @RolesAllowed("users")
//  @InputTransformer(PassthroughInputTransformer.class)
//  @OutputTransformer(XMLOutputTransformer.class)
  public Response uploadFile(@Context HttpServletRequest servletRequest,
      @QueryParam("uploadId") String uploadId) throws Exception {
    //return fileUploadHandler.upload(servletRequest, uploadId, null);
    return null;
  }

  /**
   * Return information about the upload status of a file (upload percentage, file name, and more).
   *
   * @param workspaceName The name of workspace.
   * @param driveName The name of drive.
   * @param currentFolder The path to the current folder.
   * @param currentPortal The name of the current site.
   * @param action The action to perform (saving, processing, and more).
   * @param language The language of user.
   * @param fileName The name of file.
   * @param uploadId The Id of the uploaded resource .
   *
   * @return The response.
   *
   * @throws Exception The exception
   * 
   * @anchor CONTref.Devref.PublicRestAPIs.ManageDocumentService.processUpload
   */
  
  @GET
  @Path("/uploadFile/control/")
  //@RolesAllowed("users")
  public Response processUpload(
      @QueryParam("workspaceName") String workspaceName,
      @QueryParam("driveName") String driveName,
      @QueryParam("currentFolder") String currentFolder,
      @QueryParam("currentPortal") String siteName,
      @QueryParam("action") String action,
      @QueryParam("language") String language,
      @QueryParam("fileName") String fileName,
      @QueryParam("uploadId") String uploadId) throws Exception {
    
    DateFormat dateFormat = new SimpleDateFormat(IF_MODIFIED_SINCE_DATE_FORMAT);    
    return Response.ok().header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date())).build();
  }

  
  private Response buildXMLResponseForChildren(Node node,
                                               String driveName,
                                               String currentFolder,
                                               boolean showHidden
                                               ) throws Exception {
    Document document = createNewDocument();
    Element rootElement = createFolderElement(document,
                                              node,
                                              node.getSession().getWorkspace().getName(),
                                              driveName,
                                              currentFolder);
    Element folders = document.createElement("Folders");
    Element files = document.createElement("Files");
    Node referParentNode = node;
//    if (node.isNodeType("exo:symlink") && node.hasProperty("exo:uuid") && node.hasProperty("exo:workspace")) {
//      referParentNode = linkManager.getTarget(node);
//    }
    for (NodeIterator iterator = referParentNode.getNodes(); iterator.hasNext();) {
      Node sourceNode = null;
      Node referNode = null;
      Node child = iterator.nextNode();
//      if (child.isNodeType(FCKUtils.EXO_HIDDENABLE) && !showHidden)
//        continue;
//      if (child.isNodeType("exo:symlink") && child.hasProperty("exo:uuid") && child.hasProperty("exo:workspace")) {
//        sourceNode = linkManager.getTarget(child);
//      }
      referNode = sourceNode != null ? sourceNode : child;

      if (isFolder(referNode)) {
        // Get current folder from folder path to fix same name problem (ECMS-3586)
        String folderPath = referNode.getPath();
        folderPath = folderPath.substring(folderPath.lastIndexOf("/") + 1, folderPath.length());
        String childFolder = StringUtils.isEmpty(currentFolder) ? folderPath : currentFolder.concat("/")
                                                                                    .concat(folderPath);
        Element folder = createFolderElement(document,
                                             referNode,
                                             referNode.getSession().getWorkspace().getName(),
                                             driveName,
                                             childFolder);
        folders.appendChild(folder);
      } else   if (isFile(referNode)) {
        Element file = createFileElement(document, referNode, child,
                                         referNode.getSession().getWorkspace().getName());
        files.appendChild(file);
      } else {
        continue;
      }
    }
    rootElement.appendChild(folders);
      rootElement.appendChild(files);
    document.appendChild(rootElement);
    return getResponse(document);
  }

  private boolean isFolder(Node checkNode) throws RepositoryException {
    return checkNode.isNodeType(NT_FOLDER)
        || checkNode.isNodeType(NT_UNSTRUCTURED);
  }

  private boolean isFile(Node checkNode) throws RepositoryException {
    return checkNode.isNodeType(NT_FILE);
  }

  private Element createFolderElement(Document document,
                                      Node child,
                                      String workspaceName,
                                      String driveName,
                                      String currentFolder) throws Exception {
    Element folder = document.createElement("Folder");
    boolean hasChild = false;
    boolean canRemove = true;
    boolean canAddChild = true;
    for (NodeIterator iterator = child.getNodes(); iterator.hasNext();) {
      if (isFolder(iterator.nextNode())) {
        hasChild = true;
        break;
      }
    }
    try {
      getSession(workspaceName).checkPermission(child.getPath(), PermissionType.REMOVE);
    } catch (Exception e) {
      canRemove = false;
    }

    try {
      getSession(workspaceName).checkPermission(child.getPath(), PermissionType.ADD_NODE);
    } catch (Exception e) {
      canAddChild = false;
    }

    folder.setAttribute("name", child.getName());
    folder.setAttribute("title", getTitle(child));
    folder.setAttribute("path", child.getPath());
    folder.setAttribute("canRemove", String.valueOf(canRemove));
    folder.setAttribute("canAddChild", String.valueOf(canAddChild));
    folder.setAttribute("nodeType", getNodeTypeIcon(child));
    folder.setAttribute("workspaceName", workspaceName);
    folder.setAttribute("driveName", driveName);
    folder.setAttribute("currentFolder", currentFolder);
    folder.setAttribute("hasChild", String.valueOf(hasChild));
    folder.setAttribute("titlePath", createTitlePath(driveName, workspaceName, currentFolder));

    return folder;
  }

  private Element createFileElement(Document document,
                                    Node sourceNode,
                                    Node displayNode,
                                    String workspaceName) throws Exception {
    Element file = document.createElement("File");
    boolean canRemove = true;
    file.setAttribute("name", displayNode.getName());
//    file.setAttribute("title", Utils.getTitle(displayNode));
    file.setAttribute("title", getTitle(displayNode));
    SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,
                                                                                         SimpleDateFormat.SHORT);
    file.setAttribute("dateCreated", formatter.format(sourceNode.getProperty("exo:dateCreated")
                                                                .getDate()
                                                                .getTime()));
    if (sourceNode.hasProperty("exo:dateModified")) {
      file.setAttribute("dateModified", formatter.format(sourceNode.getProperty("exo:dateModified")
                                                                   .getDate()
                                                                   .getTime()));
    } else {
      file.setAttribute("dateModified", null);
    }
    file.setAttribute("creator", sourceNode.getProperty("exo:owner").getString());
    file.setAttribute("path", displayNode.getPath());
    if (sourceNode.isNodeType("nt:file")) {
      Node content = sourceNode.getNode("jcr:content");
      file.setAttribute("nodeType", content.getProperty("jcr:mimeType").getString());
    } else {
      file.setAttribute("nodeType", sourceNode.getPrimaryNodeType().getName());
    }

    long size = sourceNode.getNode("jcr:content").getProperty("jcr:data").getLength();
    file.setAttribute("size", "" + size);
    try {
      getSession(workspaceName).checkPermission(sourceNode.getPath(), PermissionType.REMOVE);
    } catch (Exception e) {
      canRemove = false;
    }
    file.setAttribute("canRemove", String.valueOf(canRemove));
    return file;
  }

  private Node getNode(String driveName, String workspaceName, String currentFolder) throws Exception {
    Session session = getSession(workspaceName);
    //String driveHomePath = manageDriveService.getDriveByName(Text.escapeIllegalJcrChars(driveName)).getHomePath();    
    String driveHomePath = session.getRootNode().getPath();
        
    String userId = ConversationState.getCurrent().getIdentity().getUserId();
    //String drivePath = Utils.getPersonalDrivePath(driveHomePath, userId);
    String drivePath = driveHomePath;
    Node node = (Node) session.getItem(drivePath);
    if (StringUtils.isEmpty(currentFolder)) {
      return node;
    }
    for (String folder : currentFolder.split("/")) {
      node = node.getNode(folder);
//      if (node.isNodeType("exo:symlink")) {
//        node = linkManager.getTarget(node);
//      }
    }
    return node;
  }

  private Session getSession(String workspaceName) throws Exception {    
    ManageableRepository manageableRepository = getCurrentRepository();
    //return sessionProvider.getSession(workspaceName, manageableRepository);
    return manageableRepository.getSystemSession(workspaceName);
  }

  private Document createNewDocument() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.newDocument();
  }

  private ManageableRepository getCurrentRepository() throws RepositoryException {
    return repositoryService.getCurrentRepository();
  }

  private Response getResponse(Document document) {
    DateFormat dateFormat = new SimpleDateFormat(IF_MODIFIED_SINCE_DATE_FORMAT);
    return Response.ok(new DOMSource(document), MediaType.TEXT_XML)
                   .cacheControl(cc)
                   .header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date()))
                   .build();
  }

  /**
   * Build drive node from drive list.
   *
   * @param document the document.
   * @param drivesList the drives list.
   * @param driveType the drive type.
   *
   * @return the element
   */
  private Element buildXMLDriveNodes(Document document, List<String> drivesList, String driveType) throws Exception {
    Element folders = document.createElement("Folders");
    folders.setAttribute("name", driveType);
    for (String drive : drivesList) {
      Element folder = document.createElement("Folder");
      folder.setAttribute("name", drive);
      folder.setAttribute("nodeType", "workspace");
      folder.setAttribute("workspaceName", drive);
      folder.setAttribute("canAddChild", "false");
      folders.appendChild(folder);
    }
    return folders;
  }
  
  private Element buildXMLLocalSelections(Document document, List<String> locations){
    Element selections = document.createElement("Locations");    
    for (String local : locations) {
      Element folder = document.createElement("location");
      folder.setAttribute("name", local);      
      folder.setAttribute("canAddChild", "true");
      selections.appendChild(folder);
    }
    return selections;
  }

  /**
   * Gets the memberships.
   *
   * @param userId the user id.
   *
   * @return the memberships
   *
   * @throws Exception the exception
   */
  private List<String> getMemberships() throws Exception {
    List<String> userMemberships = new ArrayList<String>();
    String userId = ConversationState.getCurrent().getIdentity().getUserId();
    userMemberships.add(userId);
    Collection<?> memberships = ConversationState.getCurrent().getIdentity().getMemberships();
    if (memberships == null || memberships.size() < 0)
      return userMemberships;
    Object[] objects = memberships.toArray();
    for (int i = 0; i < objects.length; i++) {
      MembershipEntry membership = (MembershipEntry) objects[i];
      String role = membership.getMembershipType() + ":" + membership.getGroup();
      userMemberships.add(role);
    }
    return userMemberships;
  }

  public static String getNodeTypeIcon(Node node) throws RepositoryException {
    StringBuilder str = new StringBuilder();
    if (node == null)
      return "";
    String nodeType = node.getPrimaryNodeType().getName();
//    if (node.isNodeType(EXO_SYMLINK)) {
//      LinkManager linkManager = (LinkManager) ExoContainerContext.getCurrentContainer()
//                                                                 .getComponentInstanceOfType(LinkManager.class);
//      try {
//        nodeType = node.getProperty(EXO_PRIMARYTYPE).getString();
//        node = linkManager.getTarget(node);
//        if (node == null)
//          return "";
//      } catch (Exception e) {
//        return "";
//      }
//    }
    if (node.isNodeType(EXO_TRASH_FOLDER)) {
      nodeType = EXO_TRASH_FOLDER;
    }
    if (node.isNodeType(EXO_FAVOURITE_FOLDER))
      nodeType = EXO_FAVOURITE_FOLDER;
    if (nodeType.equals(NT_UNSTRUCTURED) || nodeType.equals(NT_FOLDER)) {
      for (String specificFolder : SPECIFIC_FOLDERS) {
        if (node.isNodeType(specificFolder)) {
          nodeType = specificFolder;
          break;
        }
      }
    }
    str.append(nodeType);
    return str.toString();
  }

  /**
   * Creates the process upload response.
   *
   * @param workspaceName the workspace name
   * @param jcrPath the jcr path
   * @param action the action
   * @param language the language
   * @param fileName the file name
   * @param uploadId the upload id
   * @param siteName the portal name
   * @param currentFolderNode the current folder node
   *
   * @return the response
   *
   * @throws Exception the exception
   */
  /*
  protected Response createProcessUploadResponse(String workspaceName,
                                                 Node currentFolderNode,
                                                 String siteName,
                                                 String userId,
                                                 String action,
                                                 String language,
                                                 String fileName,
                                                 String uploadId) throws Exception {
    if (FileUploadHandler.SAVE_ACTION.equals(action)) {
      CacheControl cacheControl = new CacheControl();
      cacheControl.setNoCache(true);
      DocumentContext.getCurrent().getAttributes().put(DocumentContext.IS_SKIP_RAISE_ACT, true);
      return fileUploadHandler.saveAsNTFile(currentFolderNode, uploadId, fileName, language, siteName, userId);
    }
    return fileUploadHandler.control(uploadId, action);
  }
*/
  
  private String createTitlePath(String driveName, String workspaceName, String currentFolder) throws Exception {
    String[] folders = currentFolder.split("/");
    StringBuilder sb = new StringBuilder();
    StringBuilder tempFolder = new StringBuilder();
    Node parentNode = getNode(driveName, workspaceName, "");
    if (StringUtils.isEmpty(currentFolder)) {
      return "";
    }
    for (int i = 0; i < folders.length; i++) {
      tempFolder = tempFolder.append(folders[i]);
      Node node = null;
      try {
        node = getNode(driveName, workspaceName, tempFolder.toString());
      } catch (PathNotFoundException e) {
        node = parentNode.getNode(folders[i]);
      }
      tempFolder = tempFolder.append("/");
      //sb.append(Utils.getTitle(node));
      sb.append(getTitle(node));
      if (i != folders.length - 1) {
        sb.append("/");
      }
      //parentNode = (node.isNodeType("exo:symlink")? linkManager.getTarget(node) : node);
      parentNode =  node;
    }
    return sb.toString();
  }
  
  private String getTitle(Node node) throws Exception {
    String title = null;
    if (node.hasProperty("exo:title")) {
      title = node.getProperty("exo:title").getValue().getString();
    } else if (node.hasNode("jcr:content")) {
      Node content = node.getNode("jcr:content");
      if (content.hasProperty("dc:title")) {
        try {
          title = content.getProperty("dc:title").getValues()[0].getString();
        } catch (Exception ex) {
          title = null;
        }
      }
    }
    if (title == null) {
      if (node.isNodeType("nt:frozenNode")) {
        String uuid = node.getProperty("jcr:frozenUuid").getString();
        Node originalNode = node.getSession().getNodeByUUID(uuid);
        title = originalNode.getName();
      } else {
        title = node.getName();
      }

    }
    return StringEscapeUtils.escapeHtml(Text.unescapeIllegalJcrChars(title));
  }
  
}
