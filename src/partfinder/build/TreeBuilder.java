package partfinder.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import utils.GlobalVariables;

/**
 * Created by yigli on 14-6-23.
 */
public class TreeBuilder {

    private String tableName = null;
    private String partNumber = null;
    private String description = null;
    private String mep = null;
    private String rdo = null;

    private int radioCheck = 0;
    private boolean isChildren = false;
    private List<Entry> treeList = null;
    private boolean isAllInfo = false;

    List<Map<String, String>> treeNodes = new ArrayList();

    public TreeBuilder() {
    }

    public TreeBuilder(String tableName, String partNumber, String description) {
        this.tableName = tableName;
        this.partNumber = partNumber;
        this.description = description;
    }

    public List<List<Entry>> getTreeLists() {
        DBtoTreeList db2TreeList = new DBtoTreeList(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        db2TreeList.getConnect();

        if (tableName != null && !tableName.equals("all"))
            db2TreeList.setSearchTableName(tableName); //T_5429452_2 T_6188888_23
        if (partNumber != null && !partNumber.isEmpty())
            db2TreeList.setPartNumber(partNumber);
        if (description != null && !description.isEmpty())
            db2TreeList.setDescription(description);
        if (mep != null && !mep.isEmpty())
            db2TreeList.setMep(mep);
        if (rdo != null && !rdo.isEmpty())
            db2TreeList.setRdo(rdo);
        if (isChildren)
            db2TreeList.setIfchildren(true);
        if (isAllInfo)
            db2TreeList.setAllInfo(true);
        db2TreeList.setRadioCheck(radioCheck);

        //db2TreeList.setSearchTableName("T_6188888_23");
        //db2TreeList.setKeyWord("5231800");

        List<List<Entry>> treeLists = db2TreeList.TableToTreeList();
        db2TreeList.closeAll();
        return treeLists;
    }

    public List<Map<String, String>> getTreeNodeList(List<List<Entry>> treeLists, int n) {
        treeList = treeLists.get(n);

        TreeNode root = this.build();
        Document doc;
        try {

            doc = DocumentHelper.parseText(root.toXMLString());
            Element rootElt = doc.getRootElement();
            this.toList("root0", rootElt);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }

        return treeNodes;
    }

    public int id = 0;

    public void toList(String parentid, Element parent) {
        List<Element> childs = parent.elements();
        for (int i = 0; i < childs.size(); i++) {
            Element node = childs.get(i);
            String idStr = String.valueOf(id++);

            Map<String, String> nodeMap = new HashMap();
            nodeMap.put("id", idStr);
            nodeMap.put("parentid", parentid);
            if (isAllInfo) {
                nodeMap.put("text", node.attributeValue("name")
                        + "&nbsp;&nbsp;||&nbsp;&nbsp;" + node.attributeValue("desc")
                        + "&nbsp;&nbsp;||&nbsp;&nbsp;" + node.attributeValue("mep")
                        + "&nbsp;&nbsp;||&nbsp;&nbsp;" + node.attributeValue("mfg")
                        + "&nbsp;&nbsp;||&nbsp;&nbsp;" + node.attributeValue("rdo"));
            } else {
                nodeMap.put("text", node.attributeValue("name")
                        + "&nbsp;&nbsp;||&nbsp;&nbsp;" + node.attributeValue("desc"));
            }
            treeNodes.add(nodeMap);
            if (node.elements().size() > 0) {
                toList(idStr, node);
            }
        }
    }

    public TreeNode build() {
        if (treeList == null || treeList.isEmpty()) {
            return null;
        }

        if (treeList.size() == 1) {
            return new TreeNode(treeList.get(0).treeLevel, treeList.get(0).data);
        }

        TreeNode ret = new TreeNode(treeList.get(0).treeLevel,
                treeList.get(0).data);

        ret.setChildren(builchild(treeList.subList(0, treeList.size())));
        //ret.setChildren(builchild(treeList.subList(1, treeList.size()))); // root pghpghpgh

        return ret;
    }

    List<TreeNode> builchild(List<Entry> treeList) {
        if (treeList.isEmpty()) {
            return null;
        }

        if (treeList.size() == 1) {
            List<TreeNode> ret = new ArrayList<TreeNode>();
            ret.add(new TreeNode(treeList.get(0).treeLevel,
                    treeList.get(0).data));

            return ret;
        }

        int index = findSubtree(treeList);

        if (index == -1) {
            TreeNode node = new TreeNode(treeList.get(0).treeLevel, treeList
                    .get(0).data);  // dbTableName
            List<TreeNode> children = builchild(treeList.subList(1, treeList
                    .size()));

            node.setChildren(children);
            List<TreeNode> ret = new ArrayList<TreeNode>();

            ret.add(node);

            return ret;
        }

        List<TreeNode> ret = new ArrayList<TreeNode>();

        List<Entry> first = treeList.subList(0, index);
        List<Entry> last = treeList.subList(index, treeList.size());

        ret.addAll(builchild(first));
        ret.addAll(builchild(last));
        return ret;
    }

    private int findSubtree(List<Entry> list) {

        if (list == null || list.size() < 2) {
            return -1;
        }
        int first = list.get(0).treeLevel;

        int subindex = 1;

        while (subindex < list.size()) {
            int sub = list.get(subindex).treeLevel;
            if (sub != first) {
                subindex++;
            } else {
                return subindex;
            }
        }
        return -1;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRadioCheck() {
        return radioCheck;
    }

    public void setRadioCheck(int radioCheck) {
        this.radioCheck = radioCheck;
    }

    public boolean isChildren() {
        return isChildren;
    }

    public void setChildren(boolean isChildren) {
        this.isChildren = isChildren;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMep() {
        return mep;
    }

    public void setMep(String mep) {
        this.mep = mep;
    }

    public String getRdo() {
        return rdo;
    }

    public void setRdo(String rdo) {
        this.rdo = rdo;
    }

    public boolean isAllInfo() {
        return isAllInfo;
    }

    public void setAllInfo(boolean isAllInfo) {
        this.isAllInfo = isAllInfo;
    }
}
