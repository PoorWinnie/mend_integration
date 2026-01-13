<template>
    <div class="role-edit-container">
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
        <p>載入中...</p>
      </div>
      
      <div class="panel">
        <div class="panel-header">
          <h2>{{ isCreating ? '創建角色' : '編輯角色' }}</h2>
          <p>{{ isCreating ? '創建新的用戶角色及設定權限' : '修改角色資訊及權限設定' }}</p>
        </div>
        
        <div class="panel-body">
          <form @submit.prevent="saveRole">
            <div class="form-group">
              <label for="role-name">角色名稱 <span class="required">*</span></label>
              <input 
                id="role-name" 
                v-model="roleForm.name" 
                type="text" 
                class="form-control" 
                required 
                placeholder="例如：營地管理員"
              />
              <div class="form-hint">用戶看到的角色名稱，建議使用清晰明確的名稱</div>
            </div>
            
            <div class="form-group">
              <label for="role-code">角色代碼 <span class="required">*</span></label>
              <input 
                id="role-code" 
                v-model="roleForm.code" 
                type="text" 
                class="form-control" 
                required 
                placeholder="例如：camp_owner" 
                :disabled="!isCreating"
              />
              <div class="form-hint">系統內部使用的唯一代碼，建議使用英文和底線</div>
            </div>
            
            <div class="form-group">
              <label for="role-description">角色描述</label>
              <textarea 
                id="role-description" 
                v-model="roleForm.description" 
                class="form-control" 
                rows="3" 
                placeholder="描述此角色的用途和權限範圍"
              ></textarea>
              <div class="form-hint">幫助管理員了解此角色的用途</div>
            </div>
            
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="roleForm.isDefault" 
                />
                <span>設為預設角色</span>
              </label>
              <div class="form-hint">新註冊用戶將默認獲得此角色</div>
            </div>
            
            <div class="form-divider"></div>
            
            <div class="permissions-section">
              <h3>角色權限</h3>
              <p>為此角色分配可執行的操作權限</p>
              
              <div class="search-box">
                <i class="fa fa-search"></i>
                <input 
                  type="text" 
                  v-model="permissionSearch" 
                  placeholder="搜尋權限..." 
                />
              </div>
              
              <div class="permissions-list">
                <div 
                  v-for="permission in filteredPermissions" 
                  :key="permission.id" 
                  class="permission-item"
                >
                  <label class="checkbox-label">
                    <input 
                      type="checkbox" 
                      :value="permission.id" 
                      v-model="roleForm.permissionIds" 
                    />
                    <span>{{ permission.name }}</span>
                  </label>
                  <div class="permission-code">{{ permission.code }}</div>
                </div>
                
                <div v-if="filteredPermissions.length === 0" class="no-results">
                  找不到符合條件的權限
                </div>
              </div>
            </div>
          </form>
        </div>
        
        <div class="panel-footer">
          <button 
            @click="cancelEdit" 
            class="btn btn-secondary"
          >
            取消
          </button>
          <button 
            @click="saveRole" 
            class="btn btn-primary" 
            :disabled="loading"
          >
            {{ loading ? '處理中...' : (isCreating ? '創建角色' : '儲存變更') }}
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useRoleStore } from '../../stores/role';
  import { useConfirm } from '../../composables/useConfirm';
  import { useToast } from '../../composables/useToast';
  
  const router = useRouter();
  const route = useRoute();
  const roleStore = useRoleStore();
  const confirm = useConfirm();
  const toast = useToast();
  
  const roleId = computed(() => {
    return route.params.id ? parseInt(route.params.id) : null;
  });
  
  const isCreating = computed(() => {
    return !roleId.value;
  });
  
  const loading = ref(false);
  const permissionSearch = ref('');
  
  const roleForm = ref({
    name: '',
    code: '',
    description: '',
    isDefault: false,
    permissionIds: []
  });
  
  const originalForm = ref({});
  
  const filteredPermissions = computed(() => {
    if (!permissionSearch.value.trim()) {
      return roleStore.permissions;
    }
    
    const searchTerm = permissionSearch.value.toLowerCase().trim();
    return roleStore.permissions.filter(permission => 
      permission.name.toLowerCase().includes(searchTerm) ||
      permission.code.toLowerCase().includes(searchTerm)
    );
  });
  
  // 載入角色數據
  const loadRoleData = async () => {
    if (!roleId.value) return;
    
    loading.value = true;
    try {
      const role = await roleStore.getRoleById(roleId.value);
      
      roleForm.value = {
        name: role.name,
        code: role.code,
        description: role.description || '',
        isDefault: role.isDefault || false,
        permissionIds: role.permissions.map(p => p.id)
      };
      
      // 保存原始表單數據用於檢測變更
      originalForm.value = JSON.parse(JSON.stringify(roleForm.value));
    } catch (error) {
      console.error('載入角色數據失敗:', error);
      toast.error('載入角色數據失敗: ' + (error.message || '未知錯誤'));
      router.push('/admin/roles');
    } finally {
      loading.value = false;
    }
  };
  
  // 載入權限列表
  const loadPermissions = async () => {
    if (roleStore.permissions.length === 0) {
      loading.value = true;
      try {
        await roleStore.getPermissions();
      } catch (error) {
        console.error('載入權限列表失敗:', error);
        toast.error('載入權限列表失敗: ' + (error.message || '未知錯誤'));
      } finally {
        loading.value = false;
      }
    }
  };
  
  // 保存角色
  const saveRole = async () => {
    loading.value = true;
    
    try {
      const roleData = {
        name: roleForm.value.name,
        code: roleForm.value.code,
        description: roleForm.value.description,
        isDefault: roleForm.value.isDefault,
        permissionIds: roleForm.value.permissionIds
      };
      
      if (isCreating.value) {
        await roleStore.createRole(roleData);
        toast.success('角色創建成功');
      } else {
        await roleStore.updateRole(roleId.value, roleData);
        toast.success('角色更新成功');
      }
      
      router.push('/admin/roles');
    } catch (error) {
      console.error('保存角色失敗:', error);
      toast.error('保存角色失敗: ' + (error.message || '未知錯誤'));
    } finally {
      loading.value = false;
    }
  };
  
  // 取消編輯
  const cancelEdit = async () => {
    const hasChanges = JSON.stringify(originalForm.value) !== JSON.stringify(roleForm.value);
    
    if (hasChanges) {
      const result = await confirm.show('確定要離開嗎？', '您有尚未保存的變更，離開將會丟失這些變更。');
      if (!result) return;
    }
    
    router.push('/admin/roles');
  };
  
  onMounted(async () => {
    await loadPermissions();
    await loadRoleData();
  });
  </script>
  
  <style scoped>
  .role-edit-container {
    position: relative;
  }
  
  .loading-overlay {
    position: absolute;
    inset: 0;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 10;
  }
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid var(--gray-200);
    border-top-color: var(--tiffany-blue);
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 10px;
  }
  
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
  
  .panel {
    background-color: white;
    border-radius: 8px;
    box-shadow: var(--shadow);
    overflow: hidden;
  }
  
  .panel-header {
    padding: 20px 25px;
    border-bottom: 1px solid var(--gray-100);
  }
  
  .panel-header h2 {
    margin: 0 0 8px 0;
    font-size: 20px;
    color: var(--gray-800);
  }
  
  .panel-header p {
    margin: 0;
    color: var(--gray-600);
    font-size: 14px;
  }
  
  .panel-body {
    padding: 25px;
  }
  
  .panel-footer {
    display: flex;
    justify-content: flex-end;
    padding: 20px 25px;
    border-top: 1px solid var(--gray-100);
    gap: 12px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: var(--gray-700);
  }
  
  .required {
    color: #ff4d4f;
  }
  
  .form-control {
    width: 100%;
    padding: 10px 12px;
    font-size: 14px;
    border: 1px solid var(--gray-300);
    border-radius: 6px;
    outline: none;
    transition: all 0.3s;
  }
  
  .form-control:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.3);
  }
  
  .form-control:disabled {
    background-color: var(--gray-100);
    cursor: not-allowed;
  }
  
  .form-hint {
    margin-top: 6px;
    font-size: 12px;
    color: var(--gray-500);
  }
  
  .form-divider {
    height: 1px;
    background-color: var(--gray-100);
    margin: 30px 0;
  }
  
  .checkbox-label {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
  
  .checkbox-label input {
    margin-right: 10px;
  }
  
  .permissions-section {
    margin-top: 10px;
  }
  
  .permissions-section h3 {
    margin: 0 0 8px 0;
    font-size: 16px;
    color: var(--gray-800);
  }
  
  .permissions-section p {
    margin: 0 0 15px 0;
    color: var(--gray-600);
    font-size: 14px;
  }
  
  .search-box {
    display: flex;
    align-items: center;
    background-color: var(--gray-100);
    border-radius: 6px;
    padding: 8px 12px;
    margin-bottom: 15px;
  }
  
  .search-box i {
    color: var(--gray-500);
    margin-right: 10px;
  }
  
  .search-box input {
    border: none;
    background: transparent;
    outline: none;
    width: 100%;
    color: var(--gray-700);
  }
  
  .permissions-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 12px;
    max-height: 300px;
    overflow-y: auto;
    padding: 5px 0;
  }
  
  .permission-item {
    padding: 8px 10px;
    border-radius: 6px;
    transition: background 0.3s;
  }
  
  .permission-item:hover {
    background-color: var(--gray-50);
  }
  
  .permission-code {
    margin-left: 24px;
    font-size: 11px;
    color: var(--gray-500);
  }
  
  .no-results {
    grid-column: 1 / -1;
    padding: 20px;
    text-align: center;
    color: var(--gray-500);
  }
  
  .btn {
    padding: 10px 16px;
    border-radius: 6px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
    border: none;
  }
  
  .btn-primary {
    background-color: var(--tiffany-blue);
    color: white;
  }
  
  .btn-primary:hover {
    background-color: var(--tiffany-blue-dark);
  }
  
  .btn-primary:disabled {
    background-color: var(--gray-300);
    cursor: not-allowed;
  }
  
  .btn-secondary {
    background-color: var(--gray-100);
    color: var(--gray-700);
  }
  
  .btn-secondary:hover {
    background-color: var(--gray-200);
  }
  </style>