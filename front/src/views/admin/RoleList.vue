<template>
  <div class="role-management-container">
    <div class="page-header">
      <div class="header-main">
        <h1>角色管理</h1>
        <p>管理系統角色和權限設定</p>
      </div>
      <div class="header-actions">
        <div class="search-box">
          <i class="fa fa-search"></i>
          <input type="text" placeholder="搜尋角色..." v-model="searchQuery" @input="filterRoles" />
        </div>
        <button class="btn-add" @click="openAddRoleModal">
          <i class="fa fa-plus"></i> 新增角色
        </button>
      </div>
    </div>

    <!-- 統計概覽 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon system-roles">
          <i class="fa fa-shield-alt"></i>
        </div>
        <div class="stat-content">
          <h3>系統角色</h3>
          <p class="stat-value">{{ systemRoleCount }}</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon custom-roles">
          <i class="fa fa-users-cog"></i>
        </div>
        <div class="stat-content">
          <h3>自訂角色</h3>
          <p class="stat-value">{{ customRoleCount }}</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon active-users">
          <i class="fa fa-user-check"></i>
        </div>
        <div class="stat-content">
          <h3>使用者總數</h3>
          <p class="stat-value">{{ totalUsers }}</p>
        </div>
      </div>

      <div class="stat-card clickable" @click="navigateToRoleRequests">
        <div class="stat-icon role-requests">
          <i class="fa fa-user-clock"></i>
        </div>
        <div class="stat-content">
          <h3>待審核請求</h3>
          <p class="stat-value">{{ pendingRequestsCount || 0 }}</p>
          <small class="stat-hint" v-if="pendingRequestsCount === 0">目前無待審核請求</small>
        </div>
        <div class="stat-arrow">
          <i class="fa fa-chevron-right"></i>
        </div>
      </div>
    </div>

    <!-- 角色類型過濾器 -->
    <div class="role-filters">
      <button class="filter-btn" :class="{ active: activeFilter === 'all' }" @click="setFilter('all')">
        <i class="fa fa-layer-group"></i> 全部角色
      </button>
      <button class="filter-btn" :class="{ active: activeFilter === 'system' }" @click="setFilter('system')">
        <i class="fa fa-shield-alt"></i> 系統角色
      </button>
      <button class="filter-btn" :class="{ active: activeFilter === 'custom' }" @click="setFilter('custom')">
        <i class="fa fa-users-cog"></i> 自訂角色
      </button>
      <button class="filter-btn" :class="{ active: activeFilter === 'admin' }" @click="setFilter('admin')">
        <i class="fa fa-user-shield"></i> 管理員角色
      </button>
    </div>

    <!-- 角色列表 -->
    <div v-if="!loading">
      <div v-if="filteredRoles.length > 0" class="role-grid">
        <div class="role-card" v-for="role in filteredRoles" :key="role.id" :class="getRoleCardClass(role)">
          <div class="role-type-indicator" :class="getRoleHeaderClass(role.code)"></div>
          <div class="role-header" :class="getRoleHeaderClass(role.code)">
            <div class="role-icon">
              <i :class="getRoleIcon(role.code)"></i>
            </div>
            <div class="role-header-content">
              <h2>{{ role.name }}</h2>
              <div class="role-badges">
                <span class="role-level-badge" :class="getRoleLevelClass(role.code)">
                  {{ getRoleLevelLabel(role.code) }}
                </span>
                <span v-if="role.isDefault" class="system-badge">
                  <i class="fa fa-shield-alt"></i> 系統角色
                </span>
              </div>
            </div>
          </div>

          <div class="role-body">
            <div class="role-metadata">
              <div class="metadata-item">
                <div class="metadata-label">
                  <i class="fa fa-fingerprint"></i> 角色代碼
                </div>
                <div class="metadata-value">{{ role.code }}</div>
              </div>
              <div class="metadata-item">
                <div class="metadata-label">
                  <i class="fa fa-users"></i> 使用者
                </div>
                <div class="metadata-value">{{ role.userCount || 0 }} 位</div>
              </div>
            </div>

            <p class="role-description">{{ role.description || '無角色描述' }}</p>

            <div class="permissions-section">
              <div class="section-title">
                <h3><i class="fa fa-key"></i> 權限</h3>
                <span class="permission-count">{{ role.permissions?.length || 0 }}</span>
              </div>

              <div v-if="role.permissions?.length > 0" class="permission-preview">
                <div class="permission-list">
                  <div v-for="(permission, index) in role.permissions.slice(0, 4)" :key="permission.id"
                    class="permission-tag">
                    <i class="fa fa-check-circle"></i> {{ permission.name }}
                  </div>
                </div>
                <div v-if="role.permissions.length > 4" class="more-permissions">
                  <button @click="viewAllPermissions(role)" class="btn-more">
                    <i class="fa fa-ellipsis-h"></i> 顯示全部 {{ role.permissions.length }} 個權限
                  </button>
                </div>
              </div>
              <div v-else class="empty-permissions">
                <i class="fa fa-lock"></i>
                <p>無權限</p>
              </div>
            </div>
          </div>

          <div class="role-actions">
            <button class="btn-view" @click="viewRoleDetails(role)" title="查看詳情">
              <i class="fa fa-eye"></i>
            </button>
            <button class="btn-edit" @click="editRole(role.id)" title="編輯角色">
              <i class="fa fa-edit"></i>
            </button>
            <button v-if="!role.isDefault" class="btn-delete" @click="deleteRole(role.id)" title="刪除角色">
              <i class="fa fa-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <div v-else class="empty-roles">
        <div class="empty-content">
          <i class="fa fa-users-slash"></i>
          <h3>未找到角色</h3>
          <p v-if="searchQuery">沒有符合「{{ searchQuery }}」的搜尋結果</p>
          <p v-else-if="activeFilter !== 'all'">當前類型下沒有角色</p>
          <p v-else>系統中沒有定義任何角色</p>
          <button class="btn-add-empty" @click="openAddRoleModal">
            <i class="fa fa-plus"></i> 新增角色
          </button>
        </div>
      </div>
    </div>

    <!-- 載入狀態 -->
    <div v-else class="loading-container">
      <div class="loading-spinner">
        <i class="fa fa-spinner fa-spin"></i>
      </div>
      <span>載入中...</span>
    </div>

    <!-- 角色表單模態框 -->
    <div class="modal" v-if="showRoleModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditMode ? '編輯角色' : '新增角色' }}</h2>
          <button class="btn-close" @click="closeModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitRoleForm">
            <div class="form-row">
              <div class="form-group">
                <label for="role-name">角色名稱</label>
                <input type="text" id="role-name" v-model="roleForm.name" required />
              </div>
              <div class="form-group">
                <label for="role-code">角色代碼</label>
                <input type="text" id="role-code" v-model="roleForm.code" required :disabled="isEditMode"
                  :class="{ 'disabled': isEditMode }" />
                <small v-if="isEditMode" class="form-hint">角色代碼創建後不可更改</small>
              </div>
            </div>

            <div class="form-group">
              <label for="role-description">角色描述</label>
              <textarea id="role-description" v-model="roleForm.description" rows="3"></textarea>
            </div>

            <div class="form-group">
              <div class="permission-header">
                <label>權限設定</label>
                <div class="permission-actions">
                  <button type="button" class="btn-select" @click="selectAllPermissions">全選</button>
                  <button type="button" class="btn-select" @click="deselectAllPermissions">取消全選</button>
                </div>
              </div>

              <div v-if="permissionGroups.length > 0" class="permissions-groups">
                <div v-for="(group, groupName) in permissionGroups" :key="groupName" class="permission-group">
                  <div class="group-header" @click="togglePermissionGroup(groupName)">
                    <h4>
                      <i :class="expandedGroups[groupName] ? 'fa fa-chevron-down' : 'fa fa-chevron-right'"></i>
                      {{ groupName }}
                    </h4>
                    <span class="group-count">{{ group.length }}</span>
                  </div>

                  <div v-if="expandedGroups[groupName]" class="permissions-list">
                    <div v-for="perm in group" :key="perm.id" class="permission-checkbox">
                      <input type="checkbox" :id="'perm-' + perm.id" :value="perm.id"
                        v-model="roleForm.permissionIds" />
                      <label :for="'perm-' + perm.id">
                        {{ perm.name }}
                        <span class="permission-code">{{ perm.code }}</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="permissions-checkboxes">
                <div v-for="perm in availablePermissions" :key="perm.id" class="permission-checkbox">
                  <input type="checkbox" :id="'perm-' + perm.id" :value="perm.id" v-model="roleForm.permissionIds" />
                  <label :for="'perm-' + perm.id">{{ perm.name }}</label>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label>角色特性</label>
              <div class="role-options">
                <div class="option-checkbox">
                  <input type="checkbox" id="is-default" v-model="roleForm.isDefault" />
                  <label for="is-default">系統預設角色（無法刪除）</label>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="closeModal">取消</button>
              <button type="submit" class="btn-primary">{{ isEditMode ? '儲存變更' : '建立角色' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 角色詳情模態框 -->
    <div class="modal" v-if="showRoleDetailsModal" @click.self="closeRoleDetailsModal">
      <div class="modal-content details-modal">
        <div class="modal-header" :class="getRoleHeaderClass(selectedRole?.code)">
          <div class="modal-header-content">
            <div class="role-detail-icon">
              <i :class="getRoleIcon(selectedRole?.code)"></i>
            </div>
            <div>
              <h2>{{ selectedRole?.name }}</h2>
              <span class="role-level-badge light" :class="getRoleLevelClass(selectedRole?.code)">
                {{ getRoleLevelLabel(selectedRole?.code) }}
              </span>
              <span v-if="selectedRole?.isDefault" class="system-badge light">
                <i class="fa fa-shield-alt"></i> 系統角色
              </span>
            </div>
          </div>
          <button class="btn-close" @click="closeRoleDetailsModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="role-detail-section">
            <h3><i class="fa fa-info-circle"></i> 基本信息</h3>
            <div class="detail-info">
              <div class="detail-item">
                <div class="detail-label"><i class="fa fa-fingerprint"></i> 角色代碼</div>
                <div class="detail-value">{{ selectedRole?.code || '-' }}</div>
              </div>
              <div class="detail-item">
                <div class="detail-label"><i class="fa fa-users"></i> 用戶數量</div>
                <div class="detail-value">{{ selectedRole?.userCount || 0 }} 位使用者</div>
              </div>
              <div class="detail-item">
                <div class="detail-label"><i class="fa fa-tags"></i> 類型</div>
                <div class="detail-value">
                  <span :class="selectedRole?.isDefault ? 'system-type' : 'custom-type'">
                    {{ selectedRole?.isDefault ? '系統角色' : '自訂角色' }}
                  </span>
                </div>
              </div>
              <div class="detail-item full-width">
                <div class="detail-label"><i class="fa fa-align-left"></i> 描述</div>
                <div class="detail-value description">{{ selectedRole?.description || '無角色描述' }}</div>
              </div>
            </div>
          </div>

          <div class="role-detail-section">
            <h3><i class="fa fa-key"></i> 權限列表</h3>
            <div v-if="selectedRole?.permissions?.length" class="permissions-grid">
              <div v-for="permission in selectedRole.permissions" :key="permission.id" class="permission-item">
                <div class="permission-icon">
                  <i class="fa fa-check-circle"></i>
                </div>
                <div class="permission-info">
                  <div class="permission-name">{{ permission.name }}</div>
                  <div class="permission-code">{{ permission.code }}</div>
                </div>
              </div>
            </div>
            <div v-else class="empty-permissions-detail">
              <i class="fa fa-lock"></i>
              <p>該角色未設定任何權限</p>
            </div>
          </div>

          <div class="role-detail-footer">
            <button class="btn-secondary" @click="closeRoleDetailsModal">關閉</button>
            <button class="btn-primary" @click="editSelectedRole">
              <i class="fa fa-edit"></i> 編輯角色
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 權限詳情模態框 -->
    <div class="modal" v-if="showPermissionsModal" @click.self="closePermissionsModal">
      <div class="modal-content permissions-modal">
        <div class="modal-header" :class="getRoleHeaderClass(permissionsModalRole?.code)">
          <div class="modal-header-content">
            <div class="role-detail-icon">
              <i :class="getRoleIcon(permissionsModalRole?.code)"></i>
            </div>
            <div>
              <h2>{{ permissionsModalRole?.name }} - 權限列表</h2>
              <div class="role-permission-count">
                共 {{ permissionsModalRole?.permissions?.length || 0 }} 個權限
              </div>
            </div>
          </div>
          <button class="btn-close" @click="closePermissionsModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="permissionsModalRole?.permissions?.length" class="permissions-table">
            <div class="permissions-header">
              <div class="col-name">權限名稱</div>
              <div class="col-code">權限代碼</div>
              <div class="col-desc">描述</div>
            </div>
            <div class="permissions-body">
              <div v-for="permission in permissionsModalRole.permissions" :key="permission.id" class="permission-row">
                <div class="col-name">
                  <i class="fa fa-key"></i> {{ permission.name }}
                </div>
                <div class="col-code">{{ permission.code }}</div>
                <div class="col-desc">{{ permission.description || '無描述' }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-permissions-detail">
            <i class="fa fa-lock"></i>
            <p>該角色未設定任何權限</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from '@/composables/useToast';

const router = useRouter();
const { showToast } = useToast();

// 模擬角色數據 (與使用者管理頁面人數保持同步)
const mockRoles = [
  {
    id: 1,
    name: '超級管理員',
    code: 'SUPER_ADMIN',
    description: '系統最高權限管理員，擁有所有系統功能的訪問權限。',
    isDefault: true,
    userCount: 1,
    permissions: [
      { id: 1, name: '角色管理-查看', code: 'role:view', description: '可以查看角色列表' },
      { id: 2, name: '角色管理-創建', code: 'role:create', description: '可以創建新角色' },
      { id: 3, name: '角色管理-編輯', code: 'role:edit', description: '可以編輯現有角色' },
      { id: 4, name: '角色管理-刪除', code: 'role:delete', description: '可以刪除角色' },
      { id: 5, name: '用戶管理-查看', code: 'user:view', description: '可以查看用戶列表' },
      { id: 6, name: '用戶管理-創建', code: 'user:create', description: '可以創建新用戶' },
      { id: 7, name: '用戶管理-編輯', code: 'user:edit', description: '可以編輯用戶資料' },
      { id: 8, name: '用戶管理-刪除', code: 'user:delete', description: '可以刪除用戶' },
      { id: 9, name: '系統設置-訪問', code: 'system:access', description: '可以訪問系統設置' },
      { id: 10, name: '系統日誌-查看', code: 'log:view', description: '可以查看系統日誌' }
    ]
  },
  {
    id: 2,
    name: '管理員',
    code: 'ADMIN',
    description: '系統管理員，負責日常營運和管理，具有大部分系統功能的訪問權限。',
    isDefault: true,
    userCount: 2,
    permissions: [
      { id: 1, name: '角色管理-查看', code: 'role:view', description: '可以查看角色列表' },
      { id: 3, name: '角色管理-編輯', code: 'role:edit', description: '可以編輯現有角色' },
      { id: 5, name: '用戶管理-查看', code: 'user:view', description: '可以查看用戶列表' },
      { id: 6, name: '用戶管理-創建', code: 'user:create', description: '可以創建新用戶' },
      { id: 7, name: '用戶管理-編輯', code: 'user:edit', description: '可以編輯用戶資料' },
      { id: 9, name: '系統設置-訪問', code: 'system:access', description: '可以訪問系統設置' }
    ]
  },
  {
    id: 3,
    name: '營地主',
    code: 'CAMP_OWNER',
    description: '營地管理者，負責管理自己的營地資訊、預訂和評價。',
    isDefault: true,
    userCount: 1,
    permissions: [
      { id: 11, name: '營地管理-查看', code: 'camp:view', description: '可以查看營地列表' },
      { id: 12, name: '營地管理-創建', code: 'camp:create', description: '可以創建新營地' },
      { id: 13, name: '營地管理-編輯', code: 'camp:edit', description: '可以編輯自己的營地' },
      { id: 14, name: '預約管理-查看', code: 'booking:view', description: '可以查看預約' },
      { id: 15, name: '預約管理-確認', code: 'booking:confirm', description: '可以確認預約' },
      { id: 16, name: '評價管理-回覆', code: 'review:reply', description: '可以回覆評價' }
    ]
  },
  {
    id: 4,
    name: '一般使用者',
    code: 'USER',
    description: '普通註冊用戶，可以瀏覽營地、預訂和發表評價。',
    isDefault: true,
    userCount: 1,
    permissions: [
      { id: 17, name: '營地-瀏覽', code: 'camp:browse', description: '可以瀏覽營地' },
      { id: 18, name: '預約-創建', code: 'booking:create', description: '可以創建預約' },
      { id: 19, name: '評價-發表', code: 'review:post', description: '可以發表評價' }
    ]
  }
];

// 模擬權限數據
const mockPermissions = [
  { id: 1, name: '角色管理-查看', code: 'role:view', description: '可以查看角色列表' },
  { id: 2, name: '角色管理-創建', code: 'role:create', description: '可以創建新角色' },
  { id: 3, name: '角色管理-編輯', code: 'role:edit', description: '可以編輯現有角色' },
  { id: 4, name: '角色管理-刪除', code: 'role:delete', description: '可以刪除角色' },
  { id: 5, name: '用戶管理-查看', code: 'user:view', description: '可以查看用戶列表' },
  { id: 6, name: '用戶管理-創建', code: 'user:create', description: '可以創建新用戶' },
  { id: 7, name: '用戶管理-編輯', code: 'user:edit', description: '可以編輯用戶資料' },
  { id: 8, name: '用戶管理-刪除', code: 'user:delete', description: '可以刪除用戶' },
  { id: 9, name: '系統設置-訪問', code: 'system:access', description: '可以訪問系統設置' },
  { id: 10, name: '系統日誌-查看', code: 'log:view', description: '可以查看系統日誌' },
  { id: 11, name: '營地管理-查看', code: 'camp:view', description: '可以查看營地列表' },
  { id: 12, name: '營地管理-創建', code: 'camp:create', description: '可以創建新營地' },
  { id: 13, name: '營地管理-編輯', code: 'camp:edit', description: '可以編輯自己的營地' },
  { id: 14, name: '預約管理-查看', code: 'booking:view', description: '可以查看預約' },
  { id: 15, name: '預約管理-確認', code: 'booking:confirm', description: '可以確認預約' },
  { id: 16, name: '評價管理-回覆', code: 'review:reply', description: '可以回覆評價' },
  { id: 17, name: '營地-瀏覽', code: 'camp:browse', description: '可以瀏覽營地' },
  { id: 18, name: '預約-創建', code: 'booking:create', description: '可以創建預約' },
  { id: 19, name: '評價-發表', code: 'review:post', description: '可以發表評價' }
];

// 角色數據
const roles = ref([]);
const loading = ref(false);
const pendingRequestsCount = ref(2);
const totalUsers = ref(0);

// 權限資料
const availablePermissions = ref([]);

// 搜索和過濾
const searchQuery = ref('');
const activeFilter = ref('all');
const filteredRoles = ref([]);

// 模態框狀態
const showRoleModal = ref(false);
const showRoleDetailsModal = ref(false);
const showPermissionsModal = ref(false);
const isEditMode = ref(false);
const selectedRole = ref(null);
const permissionsModalRole = ref(null);

// 權限分組狀態
const permissionGroups = ref({});
const expandedGroups = ref({});

// 角色表單
const roleForm = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  permissionIds: [],
  isDefault: false
});

// 計算屬性: 系統角色數量
const systemRoleCount = computed(() => {
  return roles.value.filter(role => role.isDefault).length;
});

// 計算屬性: 自訂角色數量
const customRoleCount = computed(() => {
  return roles.value.filter(role => !role.isDefault).length;
});

// 載入角色列表 (模擬數據)
const loadRoles = async () => {
  loading.value = true;
  try {
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 使用模擬數據
    roles.value = [...mockRoles];

    // 初始化過濾角色
    filterRoles();

    // 獲取使用者總數
    totalUsers.value = roles.value.reduce((sum, role) => sum + (role.userCount || 0), 0);
  } catch (error) {
    showToast('載入角色失敗: ' + (error.message || '未知錯誤'), 'error');
  } finally {
    loading.value = false;
  }
};

// 載入權限列表 (模擬數據)
const loadPermissions = async () => {
  try {
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 使用模擬數據
    availablePermissions.value = [...mockPermissions];

    // 將權限按功能模塊分組
    groupPermissionsByModule();
  } catch (error) {
    showToast('載入權限列表失敗: ' + (error.message || '未知錯誤'), 'error');
  }
};

// 載入角色請求數量 (模擬數據)
const loadRoleRequestsCount = async () => {
  try {
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 使用模擬數據
    pendingRequestsCount.value = 2;
  } catch (error) {
    console.error('載入角色請求數量失敗:', error);
    pendingRequestsCount.value = 0;
  }
};

// 將權限按模塊分組
const groupPermissionsByModule = () => {
  const groups = {};

  availablePermissions.value.forEach(permission => {
    // 從權限代碼中提取模塊（假設格式為 "模塊:操作"）
    const module = permission.code.includes(':')
      ? permission.code.split(':')[0].toUpperCase()
      : '其他';

    if (!groups[module]) {
      groups[module] = [];
    }

    groups[module].push(permission);
  });

  permissionGroups.value = groups;

  // 初始化所有分組為折疊狀態
  const expanded = {};
  Object.keys(groups).forEach(group => {
    expanded[group] = false;
  });

  expandedGroups.value = expanded;
};

// 切換權限分組展開/折疊
const togglePermissionGroup = (groupName) => {
  expandedGroups.value[groupName] = !expandedGroups.value[groupName];
};

// 全選權限
const selectAllPermissions = () => {
  roleForm.permissionIds = availablePermissions.value.map(perm => perm.id);
};

// 取消全選權限
const deselectAllPermissions = () => {
  roleForm.permissionIds = [];
};

// 獲取角色卡片樣式類
const getRoleCardClass = (role) => {
  const classes = [];

  if (role.isDefault) {
    classes.push('system-role');
  }

  const codeStr = String(role.code).toLowerCase();
  if (codeStr.includes('super')) {
    classes.push('super-admin-role');
  } else if (codeStr.includes('admin')) {
    classes.push('admin-role');
  }

  return classes;
};

// 根據角色代碼獲取對應的 CSS 類
const getRoleHeaderClass = (code) => {
  if (!code) return '';
  const codeStr = String(code).toLowerCase();

  if (codeStr.includes('super')) return 'super-admin';
  if (codeStr.includes('admin')) return 'admin';
  if (codeStr.includes('user') || codeStr.includes('camper')) return 'camper';
  if (codeStr.includes('owner')) return 'owner';
  if (codeStr.includes('editor')) return 'editor';
  return '';
};

// 根據角色代碼獲取角色圖標
const getRoleIcon = (code) => {
  if (!code) return 'fa fa-user';
  const codeStr = String(code).toLowerCase();

  if (codeStr.includes('super')) return 'fa fa-user-astronaut';
  if (codeStr.includes('admin')) return 'fa fa-user-shield';
  if (codeStr.includes('owner')) return 'fa fa-campground';
  if (codeStr.includes('editor')) return 'fa fa-user-edit';
  if (codeStr.includes('user') || codeStr.includes('camper')) return 'fa fa-hiking';
  return 'fa fa-user';
};

// 根據角色代碼獲取角色級別標籤
const getRoleLevelLabel = (code) => {
  if (!code) return '一般角色';
  const codeStr = String(code).toLowerCase();

  if (codeStr.includes('super')) return '超級管理員';
  if (codeStr.includes('admin')) return '管理員';
  if (codeStr.includes('owner')) return '營地主';
  if (codeStr.includes('editor')) return '內容編輯員';
  if (codeStr.includes('user') || codeStr.includes('camper')) return '一般使用者';
  return '一般角色';
};

// 根據角色代碼獲取角色級別樣式類
const getRoleLevelClass = (code) => {
  if (!code) return '';
  const codeStr = String(code).toLowerCase();

  if (codeStr.includes('super')) return 'super-admin-level';
  if (codeStr.includes('admin')) return 'admin-level';
  if (codeStr.includes('owner')) return 'owner-level';
  if (codeStr.includes('editor')) return 'editor-level';
  if (codeStr.includes('user') || codeStr.includes('camper')) return 'user-level';
  return '';
};

// 導航到角色請求頁面
const navigateToRoleRequests = () => {
  router.push('/admin/role-requests');
};

// 設置過濾器
const setFilter = (filter) => {
  activeFilter.value = filter;
  filterRoles();
};

// 過濾角色
const filterRoles = () => {
  let result = [...roles.value];

  // 按角色類型過濾
  if (activeFilter.value === 'system') {
    result = result.filter(role => role.isDefault);
  } else if (activeFilter.value === 'custom') {
    result = result.filter(role => !role.isDefault);
  } else if (activeFilter.value === 'admin') {
    result = result.filter(role => {
      const codeStr = String(role.code).toLowerCase();
      return codeStr.includes('admin') || codeStr.includes('super');
    });
  }

  // 按搜索關鍵詞過濾
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(role =>
      role.name.toLowerCase().includes(query) ||
      role.code.toLowerCase().includes(query) ||
      role.description?.toLowerCase().includes(query)
    );
  }

  filteredRoles.value = result;
};

// 監聽搜索詞變化
watch(searchQuery, () => {
  filterRoles();
});

// 查看角色詳情
const viewRoleDetails = (role) => {
  selectedRole.value = { ...role };
  showRoleDetailsModal.value = true;
};

// 查看所有權限
const viewAllPermissions = (role) => {
  permissionsModalRole.value = { ...role };
  showPermissionsModal.value = true;
};

// 關閉角色詳情模態框
const closeRoleDetailsModal = () => {
  showRoleDetailsModal.value = false;
  selectedRole.value = null;
};

// 關閉權限詳情模態框
const closePermissionsModal = () => {
  showPermissionsModal.value = false;
  permissionsModalRole.value = null;
};

// 編輯所選角色
const editSelectedRole = () => {
  if (selectedRole.value) {
    closeRoleDetailsModal();
    editRole(selectedRole.value.id);
  }
};

// 編輯角色
const editRole = async (roleId) => {
  try {
    loading.value = true;
    
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const role = roles.value.find(r => r.id === roleId);

    if (!role) {
      showToast('找不到該角色', 'error');
      return;
    }

    // 填充表單
    roleForm.id = role.id;
    roleForm.name = role.name;
    roleForm.code = role.code;
    roleForm.description = role.description || '';
    roleForm.isDefault = role.isDefault || false;
    roleForm.permissionIds = (role.permissions || []).map(p => p.id);

    isEditMode.value = true;
    showRoleModal.value = true;
  } catch (error) {
    showToast('獲取角色資料失敗: ' + (error.message || '未知錯誤'), 'error');
  } finally {
    loading.value = false;
  }
};

// 刪除角色
const deleteRole = async (roleId) => {
  if (!confirm('確定要刪除此角色嗎？這會影響所有使用該角色的使用者。')) {
    return;
  }

  try {
    loading.value = true;
    
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 檢查是否為系統預設角色
    const role = roles.value.find(r => r.id === roleId);
    if (role && role.isDefault) {
      showToast('系統預設角色不能刪除', 'error');
      return;
    }
    
    // 從數組中移除角色
    roles.value = roles.value.filter(r => r.id !== roleId);
    
    // 更新過濾結果
    filterRoles();
    
    showToast('角色已刪除', 'success');
  } catch (error) {
    showToast('刪除角色失敗: ' + (error.message || '未知錯誤'), 'error');
  } finally {
    loading.value = false;
  }
};

// 打開新增角色模態窗口
const openAddRoleModal = () => {
  // 重置表單
  roleForm.id = null;
  roleForm.name = '';
  roleForm.code = '';
  roleForm.description = '';
  roleForm.permissionIds = [];
  roleForm.isDefault = false;

  isEditMode.value = false;
  showRoleModal.value = true;
};

// 關閉模態窗口
const closeModal = () => {
  showRoleModal.value = false;
};

// 提交角色表單
const submitRoleForm = async () => {
  try {
    if (!roleForm.name || !roleForm.code) {
      showToast('請填寫角色名稱和代碼', 'warning');
      return;
    }

    // 顯示處理中的狀態
    const submitButton = document.querySelector('.modal-footer .btn-primary');
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.innerHTML = isEditMode.value ? '<i class="fa fa-spinner fa-spin"></i> 儲存中...' : '<i class="fa fa-spinner fa-spin"></i> 建立中...';
    }

    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 獲取選中權限的完整對象
    const selectedPermissions = roleForm.permissionIds.map(id => 
      availablePermissions.value.find(p => p.id === id)
    ).filter(Boolean);

    if (isEditMode.value) {
      // 編輯模式 - 更新現有角色
      const roleIndex = roles.value.findIndex(r => r.id === roleForm.id);
      if (roleIndex !== -1) {
        roles.value[roleIndex] = {
          ...roles.value[roleIndex],
          name: roleForm.name,
          description: roleForm.description,
          isDefault: roleForm.isDefault,
          permissions: selectedPermissions
        };
      }
    } else {
      // 新增模式 - 創建新角色
      const newId = Math.max(...roles.value.map(r => r.id), 0) + 1;
      const newRole = {
        id: newId,
        name: roleForm.name,
        code: roleForm.code,
        description: roleForm.description,
        isDefault: roleForm.isDefault,
        userCount: 0,
        permissions: selectedPermissions
      };
      roles.value.push(newRole);
    }

    // 更新過濾結果
    filterRoles();

    // 先關閉模態窗
    showRoleModal.value = false;

    // 顯示成功訊息
    showToast(isEditMode.value ? '角色已更新' : '角色已建立', 'success');

  } catch (error) {
    console.error('儲存角色失敗:', error);
    showToast('儲存角色失敗: ' + (error.message || '未知錯誤'), 'error');
  } finally {
    // 恢復按鈕狀態（如果模態窗還存在）
    const submitButton = document.querySelector('.modal-footer .btn-primary');
    if (submitButton) {
      submitButton.disabled = false;
      submitButton.innerHTML = isEditMode.value ? '儲存變更' : '建立角色';
    }
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('zh-TW').format(date);
};

// 模擬 role store
const useRoleStore = () => {
  return {
    getRoles: async () => {
      await new Promise(resolve => setTimeout(resolve, 800));
      return { roles: mockRoles };
    },
    getPermissions: async () => {
      await new Promise(resolve => setTimeout(resolve, 500));
      return { permissions: mockPermissions };
    },
    getPendingRoleRequests: async () => {
      await new Promise(resolve => setTimeout(resolve, 300));
      return [
        { id: 1, userId: 101, requestedRole: 'CAMP_OWNER', status: 'pending' },
        { id: 2, userId: 102, requestedRole: 'CAMP_OWNER', status: 'pending' }
      ];
    },
    getRoleById: async (id) => {
      await new Promise(resolve => setTimeout(resolve, 500));
      return mockRoles.find(r => r.id === id);
    },
    deleteRole: async (id) => {
      await new Promise(resolve => setTimeout(resolve, 800));
      return { success: true };
    },
    createRole: async (roleData) => {
      await new Promise(resolve => setTimeout(resolve, 1000));
      return {
        id: Math.max(...mockRoles.map(r => r.id)) + 1,
        ...roleData,
        userCount: 0
      };
    },
    updateRole: async (id, roleData) => {
      await new Promise(resolve => setTimeout(resolve, 1000));
      return {
        id,
        ...roleData
      };
    }
  };
};

// 初始化
onMounted(async () => {
  await loadPermissions();
  await loadRoles();
  await loadRoleRequestsCount();
});
</script>

<style scoped>
:root {
  --tiffany-blue: #81D8D0;
  --tiffany-blue-light: #A7E7E1;
  --tiffany-blue-dark: #5EBFB7;
  --tiffany-blue-bg: #F0FAFA;
  --tiffany-blue-accent: #4DB6AC;
  --admin-red: #ef4444;
  --admin-red-light: #fee2e2;
  --super-admin-purple: #8E24AA;
  --super-admin-purple-light: #F3E5F5;
  --camper-blue: #3b82f6;
  --camper-blue-light: #dbeafe;
  --owner-green: #10b981;
  --owner-green-light: #d1fae5;
  --editor-purple: #8b5cf6;
  --editor-purple-light: #ede9fe;
  --white: #ffffff;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;
  --shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-hover: 0 10px 20px rgba(129, 216, 208, 0.15);
  --radius-sm: 4px;
  --radius: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --transition: all 0.25s ease;
}

body {
  background-color: var(--gray-50);
  color: var(--gray-800);
}

.role-management-container {
  width: 100%;
  padding: 30px;
  max-width: 1600px;
  margin: 0 auto;
}

/* 頁面標題 */
.page-header {
  margin-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.header-main h1 {
  margin-bottom: 8px;
  color: var(--gray-800);
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--tiffany-blue-dark) 0%, var(--tiffany-blue) 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.header-main p {
  color: var(--gray-500);
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-box {
  position: relative;
  width: 280px;
}

.search-box i {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  font-size: 16px;
}

.search-box input {
  width: 100%;
  padding: 12px 12px 12px 44px;
  border: 1px solid var(--gray-200);
  border-radius: 30px;
  background-color: var(--white);
  font-size: 15px;
  transition: var(--transition);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
}

.search-box input:focus {
  border-color: var(--tiffany-blue);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.15);
  outline: none;
}

.btn-add {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: var(--white);
  padding: 12px 20px;
  border-radius: 30px;
  border: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: var(--transition);
  white-space: nowrap;
  box-shadow: 0 4px 6px rgba(129, 216, 208, 0.2);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(129, 216, 208, 0.3);
}

.btn-add:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(129, 216, 208, 0.2);
}

/* 統計概覽 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 36px;
}

.stat-card {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  gap: 18px;
  transition: var(--transition);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(229, 231, 235, 0.5);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, transparent);
  transition: var(--transition);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(129, 216, 208, 0.15);
  border-color: rgba(129, 216, 208, 0.3);
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  background-color: var(--tiffany-blue-bg);
}

.stat-card:nth-child(1):hover::before {
  background: linear-gradient(90deg, var(--tiffany-blue), var(--tiffany-blue-dark));
}

.stat-card:nth-child(2):hover::before {
  background: linear-gradient(90deg, var(--owner-green), #0d9488);
}

.stat-card:nth-child(3):hover::before {
  background: linear-gradient(90deg, var(--camper-blue), #2563eb);
}

.stat-card:nth-child(4):hover::before {
  background: linear-gradient(90deg, var(--admin-red), #dc2626);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: var(--white);
  flex-shrink: 0;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.08);
}

.stat-icon.system-roles {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
}

.stat-icon.custom-roles {
  background: linear-gradient(135deg, var(--owner-green) 0%, #0d9488 100%);
}

.stat-icon.active-users {
  background: linear-gradient(135deg, var(--camper-blue) 0%, #2563eb 100%);
}

.stat-icon.role-requests {
  background: linear-gradient(135deg, var(--admin-red) 0%, #dc2626 100%);
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  font-size: 15px;
  color: var(--gray-600);
  margin: 0 0 6px 0;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0;
  line-height: 1.2;
}

.stat-hint {
  display: block;
  font-size: 12px;
  color: var(--gray-500);
  margin-top: 5px;
}

.stat-arrow {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  transition: var(--transition);
  background-color: var(--gray-100);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.clickable:hover .stat-arrow {
  transform: translateY(-50%) translateX(3px);
  color: var(--gray-600);
  background-color: var(--tiffany-blue-light);
}

/* 角色過濾器 */
.role-filters {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 10px 18px;
  border-radius: 30px;
  border: 1px solid var(--gray-200);
  background-color: var(--white);
  color: var(--gray-600);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
}

.filter-btn i {
  font-size: 12px;
}

.filter-btn:hover {
  background-color: var(--gray-100);
  border-color: var(--gray-300);
  color: var(--gray-700);
}

.filter-btn.active {
  background-color: var(--tiffany-blue-bg);
  border-color: var(--tiffany-blue);
  color: var(--tiffany-blue-dark);
  font-weight: 600;
  box-shadow: 0 2px 5px rgba(129, 216, 208, 0.15);
}

/* 角色列表 */
.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.role-card {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: var(--transition);
  border: 1px solid var(--gray-100);
  position: relative;
}

.role-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-hover);
  border-color: var(--tiffany-blue-light);
}

.role-type-indicator {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(90deg, var(--tiffany-blue), var(--tiffany-blue-dark));
}

.role-type-indicator.super-admin {
  background: linear-gradient(90deg, var(--super-admin-purple), #6A1B9A);
}

.role-type-indicator.admin {
  background: linear-gradient(90deg, var(--admin-red), #dc2626);
}

.role-type-indicator.camper {
  background: linear-gradient(90deg, var(--camper-blue), #2563eb);
}

.role-type-indicator.owner {
  background: linear-gradient(90deg, var(--owner-green), #059669);
}

.role-type-indicator.editor {
  background: linear-gradient(90deg, var(--editor-purple), #7c3aed);
}

.role-card.system-role {
  border: 1px solid rgba(129, 216, 208, 0.3);
}

.role-card.super-admin-role {
  border: 1px solid rgba(142, 36, 170, 0.2);
}

.role-card.admin-role {
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.role-header {
  padding: 25px 25px 20px;
  color: var(--gray-800);
  display: flex;
  gap: 18px;
  align-items: flex-start;
  background-color: var(--white);
  margin-top: 5px;
}

.role-icon {
  width: 50px;
  height: 50px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
  box-shadow: 0 4px 8px rgba(129, 216, 208, 0.15);
  flex-shrink: 0;
}

.role-header.super-admin .role-icon {
  background-color: var(--super-admin-purple-light);
  color: var(--super-admin-purple);
  box-shadow: 0 4px 8px rgba(142, 36, 170, 0.15);
}

.role-header.admin .role-icon {
  background-color: var(--admin-red-light);
  color: var(--admin-red);
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.15);
}

.role-header.camper .role-icon {
  background-color: var(--camper-blue-light);
  color: var(--camper-blue);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.15);
}

.role-header.owner .role-icon {
  background-color: var(--owner-green-light);
  color: var(--owner-green);
  box-shadow: 0 4px 8px rgba(16, 185, 129, 0.15);
}

.role-header.editor .role-icon {
  background-color: var(--editor-purple-light);
  color: var(--editor-purple);
  box-shadow: 0 4px 8px rgba(139, 92, 246, 0.15);
}

.role-header-content {
  flex: 1;
}

.role-header h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
  font-weight: 600;
  color: var(--gray-800);
}

.role-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.role-level-badge {
  display: inline-flex;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
}

.role-level-badge.super-admin-level {
  background-color: var(--super-admin-purple-light);
  color: var(--super-admin-purple);
}

.role-level-badge.admin-level {
  background-color: var(--admin-red-light);
  color: var(--admin-red);
}

.role-level-badge.owner-level {
  background-color: var(--owner-green-light);
  color: var(--owner-green);
}

.role-level-badge.editor-level {
  background-color: var(--editor-purple-light);
  color: var(--editor-purple);
}

.role-level-badge.user-level {
  background-color: var(--camper-blue-light);
  color: var(--camper-blue);
}

.role-level-badge.light {
  background-color: rgba(255, 255, 255, 0.25);
  color: white;
}

.system-badge {
  background-color: rgba(129, 216, 208, 0.15);
  color: var(--tiffany-blue-dark);
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 20px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.system-badge i {
  font-size: 11px;
}

.system-badge.light {
  background-color: rgba(255, 255, 255, 0.25);
  color: white;
}

.role-body {
  padding: 0 25px 20px;
  flex: 1;
}

.role-metadata {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.metadata-item {
  flex: 1;
}

.metadata-label {
  font-size: 13px;
  color: var(--gray-500);
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.metadata-label i {
  font-size: 12px;
  color: var(--tiffany-blue);
}

.metadata-value {
  font-size: 15px;
  color: var(--gray-700);
  font-weight: 500;
}

.role-description {
  margin-bottom: 20px;
  color: var(--gray-600);
  line-height: 1.6;
  font-size: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  position: relative;
  background-color: var(--gray-50);
  padding: 15px;
  border-radius: var(--radius);
  border-left: 3px solid var(--tiffany-blue-light);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.permissions-section h3 {
  font-size: 16px;
  color: var(--gray-700);
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.permissions-section h3 i {
  font-size: 14px;
  color: var(--tiffany-blue);
}

.permission-count {
  background-color: var(--gray-100);
  color: var(--gray-600);
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
}

.permission-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.permission-tag {
  display: flex;
  align-items: center;
  color: var(--gray-700);
  padding: 8px 12px;
  background-color: var(--gray-50);
  border-radius: 8px;
  font-size: 13px;
  border: 1px solid var(--gray-200);
  transition: var(--transition);
}

.permission-tag:hover {
  background-color: var(--tiffany-blue-bg);
  border-color: var(--tiffany-blue-light);
  transform: translateY(-2px);
}

.permission-tag i {
  color: var(--tiffany-blue);
  margin-right: 8px;
  font-size: 12px;
}

.more-permissions {
  margin-top: 15px;
  text-align: center;
}

.btn-more {
  background: none;
  border: none;
  color: var(--tiffany-blue-dark);
  font-size: 14px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: var(--radius);
  transition: var(--transition);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-more:hover {
  background-color: var(--tiffany-blue-bg);
}

.empty-permissions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  background-color: var(--gray-50);
  border-radius: var(--radius);
  color: var(--gray-400);
}

.empty-permissions i {
  font-size: 24px;
  margin-bottom: 10px;
}

.empty-permissions p {
  margin: 0;
  font-size: 14px;
}

.role-actions {
  padding: 15px 25px;
  display: flex;
  gap: 10px;
  border-top: 1px solid var(--gray-100);
  justify-content: flex-end;
}

.role-actions button {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
  font-size: 16px;
}

.btn-view {
  background-color: var(--gray-100);
  color: var(--gray-600);
}

.btn-view:hover {
  background-color: var(--gray-200);
  color: var(--gray-800);
  transform: translateY(-3px);
}

.btn-edit {
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
}

.btn-edit:hover {
  background-color: var(--tiffany-blue-light);
  color: var(--tiffany-blue-dark);
  transform: translateY(-3px);
}

.btn-delete {
  background-color: var(--admin-red-light);
  color: var(--admin-red);
}

.btn-delete:hover {
  background-color: #fecaca;
  transform: translateY(-3px);
}

/* 空狀態 */
.empty-roles {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  padding: 80px 20px;
  text-align: center;
}

.empty-content {
  max-width: 400px;
  margin: 0 auto;
}

.empty-content i {
  font-size: 60px;
  color: var(--gray-300);
  margin-bottom: 20px;
}

.empty-content h3 {
  font-size: 24px;
  color: var(--gray-700);
  margin: 0 0 15px 0;
}

.empty-content p {
  color: var(--gray-500);
  margin: 0 0 30px 0;
  font-size: 16px;
}

.btn-add-empty {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: var(--white);
  padding: 15px 30px;
  border-radius: 30px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: 0 6px 12px rgba(129, 216, 208, 0.2);
}

.btn-add-empty:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(129, 216, 208, 0.3);
}

/* 載入狀態 */
.loading-container {
  padding: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

.loading-spinner {
  font-size: 50px;
  color: var(--tiffany-blue);
  margin-bottom: 20px;
}

.loading-container span {
  font-size: 18px;
  color: var(--gray-500);
}

/* 模態框 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  backdrop-filter: blur(5px);
}

.modal-content {
  background-color: white;
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
}

.modal-content::-webkit-scrollbar {
  width: 8px;
}

.modal-content::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 10px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 10px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: var(--gray-400);
}

.details-modal {
  max-width: 700px;
}

.permissions-modal {
  max-width: 800px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px;
  border-bottom: 1px solid var(--gray-100);
  background-color: var(--white);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.modal-header.super-admin,
.modal-header.admin,
.modal-header.camper,
.modal-header.owner,
.modal-header.editor {
  color: white;
  border-bottom: none;
}

.modal-header.super-admin {
  background: linear-gradient(135deg, var(--super-admin-purple) 0%, #6A1B9A 100%);
}

.modal-header.admin {
  background: linear-gradient(135deg, var(--admin-red) 0%, #dc2626 100%);
}

.modal-header.camper {
  background: linear-gradient(135deg, var(--camper-blue) 0%, #2563eb 100%);
}

.modal-header.owner {
  background: linear-gradient(135deg, var(--owner-green) 0%, #059669 100%);
}

.modal-header.editor {
  background: linear-gradient(135deg, var(--editor-purple) 0%, #7c3aed 100%);
}

.modal-header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role-detail-icon {
  width: 50px;
  height: 50px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.modal-header h2 {
  margin: 0 0 6px 0;
  font-size: 22px;
  font-weight: 600;
}

.role-permission-count {
  font-size: 14px;
  opacity: 0.8;
}

.btn-close {
  background: none;
  border: none;
  font-size: 18px;
  color: currentColor;
  opacity: 0.6;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.btn-close:hover {
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.1);
}

.modal-header:not(.super-admin):not(.admin):not(.camper):not(.owner):not(.editor) .btn-close:hover {
  background-color: var(--gray-100);
}

.modal-body {
  padding: 25px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 15px;
  font-weight: 500;
  color: var(--gray-700);
}

.form-hint {
  display: block;
  color: var(--gray-500);
  font-size: 13px;
  margin-top: 6px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  font-size: 15px;
  transition: var(--transition);
  background-color: var(--white);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: var(--tiffany-blue);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.form-group input.disabled {
  background-color: var(--gray-50);
  color: var(--gray-500);
  cursor: not-allowed;
}

.permission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.permission-actions {
  display: flex;
  gap: 8px;
}

.btn-select {
  background: none;
  border: none;
  font-size: 14px;
  color: var(--tiffany-blue-dark);
  cursor: pointer;
  padding: 5px 10px;
  border-radius: var(--radius-sm);
  transition: var(--transition);
}

.btn-select:hover {
  background-color: var(--tiffany-blue-bg);
}

.permissions-groups {
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  max-height: 350px;
  overflow-y: auto;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.02);
}

.permissions-groups::-webkit-scrollbar {
  width: 8px;
}

.permissions-groups::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 10px;
}

.permissions-groups::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 10px;
}

.permissions-groups::-webkit-scrollbar-thumb:hover {
  background: var(--gray-400);
}

.permission-group {
  border-bottom: 1px solid var(--gray-200);
}

.permission-group:last-child {
  border-bottom: none;
}

.group-header {
  padding: 14px 18px;
  background-color: var(--gray-50);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: var(--transition);
}

.group-header:hover {
  background-color: var(--tiffany-blue-bg);
}

.group-header h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--gray-700);
  display: flex;
  align-items: center;
  gap: 8px;
}

.group-count {
  background-color: var(--gray-200);
  color: var(--gray-600);
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
}

.permissions-list {
  padding: 15px 18px;
  border-top: 1px solid var(--gray-200);
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.permissions-checkboxes {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  padding: 18px;
  background-color: var(--gray-50);
  border-radius: var(--radius);
  max-height: 300px;
  overflow-y: auto;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.02);
}

.permission-checkbox,
.option-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.permission-checkbox input,
.option-checkbox input {
  width: auto;
  margin-top: 3px;
}

.permission-checkbox label {
  margin: 0;
  font-weight: normal;
  line-height: 1.4;
  font-size: 14px;
}

.permission-code {
  display: block;
  font-size: 12px;
  color: var(--gray-500);
  font-family: monospace;
  margin-top: 3px;
}

.role-options {
  padding: 18px;
  background-color: var(--gray-50);
  border-radius: var(--radius);
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.02);
}

.option-checkbox label {
  margin: 0;
  font-weight: normal;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: var(--radius);
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: white;
  box-shadow: 0 4px 6px rgba(129, 216, 208, 0.2);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(129, 216, 208, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(129, 216, 208, 0.2);
}

.btn-secondary {
  background-color: var(--gray-100);
  color: var(--gray-700);
}

.btn-secondary:hover {
  background-color: var(--gray-200);
}

/* 角色詳情模態框 */
.role-detail-section {
  margin-bottom: 35px;
}

.role-detail-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--gray-700);
  padding-bottom: 10px;
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-detail-section h3 i {
  color: var(--tiffany-blue);
}

.detail-info {
  display: flex;
  flex-wrap: wrap;
  gap: 24px 30px;
}

.detail-item {
  flex: 1 0 calc(50% - 30px);
}

.detail-item.full-width {
  flex: 1 0 100%;
}

.detail-label {
  font-size: 14px;
  color: var(--gray-500);
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-label i {
  font-size: 12px;
  color: var(--tiffany-blue);
}

.detail-value {
  font-size: 16px;
  color: var(--gray-700);
  font-weight: 500;
}

.detail-value.description {
  font-weight: normal;
  line-height: 1.6;
  background-color: var(--gray-50);
  padding: 15px;
  border-radius: var(--radius);
  white-space: pre-line;
  font-size: 15px;
  border-left: 3px solid var(--tiffany-blue-light);
}

.system-type,
.custom-type {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.system-type {
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
}

.custom-type {
  background-color: var(--gray-100);
  color: var(--gray-600);
}

.permissions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.permission-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background-color: var(--gray-50);
  padding: 15px;
  border-radius: var(--radius);
  border: 1px solid var(--gray-100);
  transition: var(--transition);
}

.permission-item:hover {
  background-color: var(--tiffany-blue-bg);
  border-color: var(--tiffany-blue-light);
  transform: translateY(-3px);
}

.permission-icon {
  color: var(--tiffany-blue);
  font-size: 15px;
  margin-top: 2px;
}

.permission-info {
  flex: 1;
}

.permission-name {
  font-size: 15px;
  color: var(--gray-700);
  margin-bottom: 5px;
  line-height: 1.4;
  font-weight: 500;
}

.permission-code {
  font-size: 12px;
  color: var(--gray-500);
  font-family: monospace;
}

.empty-permissions-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  background-color: var(--gray-50);
  border-radius: var(--radius);
}

.empty-permissions-detail i {
  font-size: 40px;
  color: var(--gray-300);
  margin-bottom: 16px;
}

.empty-permissions-detail p {
  color: var(--gray-500);
  margin: 0;
  font-size: 16px;
}

.role-detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 35px;
  border-top: 1px solid var(--gray-200);
  padding-top: 25px;
}

/* 權限詳情模態框 */
.permissions-table {
  width: 100%;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.permissions-header {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  gap: 12px;
  padding: 15px 20px;
  background-color: var(--gray-50);
  font-weight: 600;
  color: var(--gray-700);
  border-bottom: 1px solid var(--gray-200);
}

.permissions-body {
  max-height: 500px;
  overflow-y: auto;
}

.permissions-body::-webkit-scrollbar {
  width: 8px;
}

.permissions-body::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 10px;
}

.permissions-body::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 10px;
}

.permissions-body::-webkit-scrollbar-thumb:hover {
  background: var(--gray-400);
}

.permission-row {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  gap: 12px;
  padding: 15px 20px;
  border-bottom: 1px solid var(--gray-100);
  transition: var(--transition);
}

.permission-row:last-child {
  border-bottom: none;
}

.permission-row:hover {
  background-color: var(--tiffany-blue-bg);
}

.col-name {
  font-weight: 500;
  color: var(--gray-700);
  display: flex;
  align-items: center;
  gap: 8px;
}

.col-name i {
  font-size: 12px;
  color: var(--tiffany-blue);
}

.col-code {
  font-family: monospace;
  color: var(--gray-600);
  font-size: 14px;
}

.col-desc {
  color: var(--gray-600);
  font-size: 14px;
  line-height: 1.4;
}

/* 響應式設計 */
@media (max-width: 1200px) {
  .role-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media (max-width: 992px) {
  .role-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .permission-row {
    grid-template-columns: 1fr 1fr;
  }

  .permissions-header {
    grid-template-columns: 1fr 1fr;
  }

  .col-desc {
    display: none;
  }
}

@media (max-width: 768px) {
  .role-management-container {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .btn-add {
    width: 100%;
    justify-content: center;
  }

  .stats-overview {
    grid-template-columns: repeat(auto-fit, minmax(100%, 1fr));
    gap: 15px;
  }

  .role-grid {
    grid-template-columns: 1fr;
  }

  .detail-item {
    flex: 1 0 100%;
  }

  .permissions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .role-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .role-badges {
    justify-content: center;
  }

  .role-metadata {
    flex-direction: column;
    gap: 12px;
  }

  .role-actions {
    justify-content: center;
  }

  .role-filters {
    overflow-x: auto;
    padding-bottom: 10px;
    justify-content: flex-start;
  }

  .filter-btn {
    white-space: nowrap;
    padding: 8px 14px;
  }

  .permissions-list {
    grid-template-columns: 1fr;
  }

  .permission-row {
    grid-template-columns: 1fr;
    padding: 15px;
  }

  .permissions-header {
    grid-template-columns: 1fr;
  }

  .col-code {
    margin-top: 6px;
  }

  .modal-header-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>