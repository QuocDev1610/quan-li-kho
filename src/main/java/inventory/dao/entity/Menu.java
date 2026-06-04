package inventory.dao.entity;

import javax.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Column(name = "url", nullable = false, length = 100)
    private String url;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ColumnDefault("1")
    @Column(name = "active_flag", nullable = false)
    private Integer activeFlag;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CREATE_DATE", nullable = false)
    private Instant createDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "UPDATE_DATE", nullable = false)
    private Instant updateDate;

    @OneToMany(mappedBy = "menu")
    private Set<Auth> auths = new LinkedHashSet<>();
    @Transient
    private List<Menu> children;
    @Transient
    private String idMenu;
    @Transient
    private TreeMap<Integer, Integer> childrenMap;

    public TreeMap<Integer, Integer> getChildrenMap() {
        return childrenMap;
    }

    public void setChildrenMap(TreeMap<Integer, Integer> childrenMap) {
        this.childrenMap = childrenMap;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    public Set<Auth> getAuths() {
        return auths;
    }

    public void setAuths(Set<Auth> auths) {
        this.auths = auths;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

}