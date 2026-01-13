<template>
  <div class="permission-management">
    <!-- 標籤頁導航 -->
    <div class="tabs-container">
      <div class="tabs">
        <button :class="['tab', activeTab === 'functions' ? 'active' : '']" @click="activeTab = 'functions'">
          <i class="fa fa-sitemap"></i> 功能權限管理
        </button>
        <button :class="['tab', activeTab === 'templates' ? 'active' : '']" @click="activeTab = 'templates'">
          <i class="fa fa-copy"></i> 權限模板
        </button>
        <button :class="['tab', activeTab === 'audit' ? 'active' : '']" @click="activeTab = 'audit'">
          <i class="fa fa-history"></i> 變更歷史
        </button>
      </div>
    </div>

    <!-- 功能權限管理頁面 -->
    <div v-if="activeTab === 'functions'" class="tab-content">
      <div class="page-header">
        <h1>功能權限管理</h1>
        <button class="btn-primary" @click="showAddProgramModal = true">
          <i class="fa fa-plus"></i> 新增功能
        </button>
      </div>
  
      <!-- 功能列表 -->
      <div class="program-tree-view" v-if="!loading">
        <div class="tree-toolbar">
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜尋功能..." 
              @input="filterPrograms"
            />
            <i class="fa fa-search"></i>
          </div>
          <div class="view-options">
            <button 
              :class="['view-option', { active: viewMode === 'tree' }]" 
              @click="viewMode = 'tree'"
            >
              <i class="fa fa-sitemap"></i> 樹狀檢視
            </button>
            <button 
              :class="['view-option', { active: viewMode === 'list' }]" 
              @click="viewMode = 'list'"
            >
              <i class="fa fa-list"></i> 列表檢視
            </button>
            <button 
              :class="['view-option', { active: viewMode === 'visual' }]" 
              @click="viewMode = 'visual'"
            >
              <i class="fa fa-project-diagram"></i> 視覺化檢視
            </button>
          </div>
        </div>
  
        <!-- 樹狀檢視模式 -->
        <div class="tree-view" v-if="viewMode === 'tree' && !searchQuery">
          <div 
            v-for="program in rootPrograms" 
            :key="program.id" 
            class="tree-item"
          >
            <div class="tree-item-header" @click="toggleProgram(program.id)">
              <i 
                :class="['fa', expandedNodes[program.id] ? 'fa-minus-square' : 'fa-plus-square']"
              ></i>
              <span class="tree-item-name">{{ program.program_name }}</span>
              <div class="tree-item-actions">
                <button class="btn-icon" @click.stop="editProgram(program)">
                  <i class="fa fa-edit"></i>
                </button>
                <button class="btn-icon" @click.stop="addChildProgram(program)">
                  <i class="fa fa-plus"></i>
                </button>
                <button class="btn-icon" @click.stop="deleteProgram(program)" :disabled="hasChildren(program)">
                  <i class="fa fa-trash"></i>
                </button>
              </div>
            </div>
            <div class="tree-item-children" v-if="expandedNodes[program.id]">
              <div 
                v-for="child in getChildrenPrograms(program.id)" 
                :key="child.id" 
                class="tree-item tree-child-item"
              >
                <div class="tree-item-header">
                  <i class="fa fa-circle" style="font-size: 8px;"></i>
                  <span class="tree-item-name">{{ child.program_name }}</span>
                  <small class="permission-type">{{ child.permission_type }}</small>
                  <div class="tree-item-actions">
                    <button class="btn-icon" @click.stop="editProgram(child)">
                      <i class="fa fa-edit"></i>
                    </button>
                    <button class="btn-icon" @click.stop="deleteProgram(child)">
                      <i class="fa fa-trash"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 列表檢視模式或搜尋結果 -->
        <div class="list-view" v-if="viewMode === 'list' || searchQuery">
          <table class="programs-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>功能名稱</th>
                <th>功能代碼</th>
                <th>URL</th>
                <th>權限類型</th>
                <th>父功能</th>
                <th>狀態</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="program in filteredPrograms" :key="program.id">
                <td>{{ program.id }}</td>
                <td>{{ program.program_name }}</td>
                <td>{{ program.program_code }}</td>
                <td>{{ program.program_url }}</td>
                <td>{{ program.permission_type }}</td>
                <td>
                  {{ program.parent_id ? getProgramName(program.parent_id) : '-' }}
                </td>
                <td>
                  <span :class="['status-badge', program.status === 1 ? 'status-active' : 'status-disabled']">
                    {{ program.status === 1 ? '啟用' : '停用' }}
                  </span>
                </td>
                <td class="actions">
                  <button class="btn-icon" @click="editProgram(program)">
                    <i class="fa fa-edit"></i>
                  </button>
                  <button 
                    class="btn-icon" 
                    @click="deleteProgram(program)"
                    :disabled="hasChildren(program)"
                  >
                    <i class="fa fa-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 視覺化檢視模式 -->
        <div class="visual-view" v-if="viewMode === 'visual'">
          <div class="visual-container">
            <div class="viz-toolbar">
              <button :class="['viz-option', {active: vizType === 'tree'}]" @click="vizType = 'tree'">
                <i class="fa fa-tree"></i> 樹狀圖
              </button>
              <button :class="['viz-option', {active: vizType === 'network'}]" @click="vizType = 'network'">
                <i class="fa fa-project-diagram"></i> 網絡圖
              </button>
              <button :class="['viz-option', {active: vizType === 'sunburst'}]" @click="vizType = 'sunburst'">
                <i class="fa fa-sun"></i> 旭日圖
              </button>
            </div>
            <div class="visual-graph-container">
              <div class="visual-placeholder">
                <i class="fa fa-project-diagram"></i>
                <p>權限關係視覺化圖表</p>
                <small>此功能需整合 D3.js 或 ECharts 等視覺化庫</small>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 無資料時顯示 -->
        <div class="empty-state" v-if="(viewMode === 'tree' && rootPrograms.length === 0 && !searchQuery) || (filteredPrograms.length === 0 && (viewMode === 'list' || searchQuery))">
          <i class="fa fa-sitemap"></i>
          <p>沒有找到功能權限項目</p>
          <button class="btn-primary" @click="showAddProgramModal = true">
            新增功能
          </button>
        </div>
      </div>
      
      <!-- 載入中 -->
      <div class="loading-state" v-else>
        <i class="fa fa-spinner fa-spin"></i> 載入中...
      </div>
    </div>

    <!-- 權限模板頁面 -->
    <div v-if="activeTab === 'templates'" class="tab-content">
      <div class="page-header">
        <h1>權限模板管理</h1>
        <button class="btn-primary" @click="showTemplateModal = true">
          <i class="fa fa-plus"></i> 新增模板
        </button>
      </div>

      <div class="template-grid" v-if="!loadingTemplates">
        <div class="template-card" v-for="template in permissionTemplates" :key="template.id">
          <div class="template-header">
            <h3>{{ template.name }}</h3>
            <span :class="['template-badge', template.is_system ? 'system' : 'custom']">
              {{ template.is_system ? '系統' : '自訂' }}
            </span>
          </div>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-meta">
            <span><i class="fa fa-key"></i> {{ template.permissions_count }} 個權限</span>
            <span><i class="fa fa-calendar-alt"></i> {{ formatDate(template.created_at) }}</span>
          </div>
          <div class="template-actions">
            <button class="btn-outline" @click="previewTemplate(template)">
              <i class="fa fa-eye"></i> 預覽
            </button>
            <button class="btn-outline" @click="applyTemplate(template)">
              <i class="fa fa-check"></i> 套用至角色
            </button>
            <button class="btn-outline btn-danger" v-if="!template.is_system" @click="deleteTemplate(template)">
              <i class="fa fa-trash"></i>
            </button>
          </div>
        </div>

        <div class="template-card add-template" @click="showTemplateModal = true">
          <div class="add-template-content">
            <i class="fa fa-plus-circle"></i>
            <p>建立新模板</p>
          </div>
        </div>
      </div>

      <div class="loading-state" v-else>
        <i class="fa fa-spinner fa-spin"></i> 載入中...
      </div>
    </div>

    <!-- 變更歷史頁面 -->
    <div v-if="activeTab === 'audit'" class="tab-content">
      <div class="page-header">
        <h1>權限變更歷史</h1>
        <div class="header-actions">
          <button class="btn-outline" @click="refreshAuditLogs">
            <i class="fa fa-sync"></i> 重新整理
          </button>
          <button class="btn-outline" @click="exportAuditLogs">
            <i class="fa fa-download"></i> 匯出日誌
          </button>
        </div>
      </div>

      <div class="audit-filters">
        <div class="filter-group">
          <label>日期範圍</label>
          <div class="date-range">
            <input type="date" v-model="auditFilters.startDate">
            <span>至</span>
            <input type="date" v-model="auditFilters.endDate">
          </div>
        </div>
        <div class="filter-group">
          <label>操作用戶</label>
          <select v-model="auditFilters.userId">
            <option value="">所有用戶</option>
            <option v-for="user in users" :key="user.id" :value="user.id">{{ user.username }}</option>
          </select>
        </div>
        <div class="filter-group">
          <label>操作類型</label>
          <select v-model="auditFilters.actionType">
            <option value="">所有類型</option>
            <option value="CREATE">新增</option>
            <option value="UPDATE">修改</option>
            <option value="DELETE">刪除</option>
            <option value="ASSIGN">權限分配</option>
          </select>
        </div>
        <button class="btn-primary" @click="searchAuditLogs">
          <i class="fa fa-search"></i> 查詢
        </button>
      </div>

      <div class="audit-timeline" v-if="!loadingAudit && auditLogs.length > 0">
        <div v-for="(audit, index) in auditLogs" :key="index" class="timeline-item">
          <div class="timeline-point" :class="getActionClass(audit.action_type)"></div>
          <div class="timeline-content">
            <div class="timeline-header">
              <span class="timeline-time">{{ formatDateTime(audit.created_at) }}</span>
              <span :class="['action-badge', getActionClass(audit.action_type)]">
                {{ getActionLabel(audit.action_type) }}
              </span>
            </div>
            <div class="timeline-body">
              <div class="user-info">
                <i class="fa fa-user"></i> {{ audit.user_name }}
              </div>
              <p class="audit-message">{{ audit.message }}</p>
              <div class="audit-details" v-if="audit.details">
                <div class="details-toggle" @click="toggleAuditDetails(index)">
                  <i :class="['fa', expandedAudit[index] ? 'fa-chevron-down' : 'fa-chevron-right']"></i>
                  詳細資訊
                </div>
                <div class="details-content" v-if="expandedAudit[index]">
                  <pre>{{ formatAuditDetails(audit.details) }}</pre>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else-if="!loadingAudit && auditLogs.length === 0">
        <i class="fa fa-history"></i>
        <p>沒有找到符合條件的變更記錄</p>
      </div>

      <div class="loading-state" v-else>
        <i class="fa fa-spinner fa-spin"></i> 載入中...
      </div>
    </div>
      
    <!-- 新增功能模態框 -->
    <div class="modal" v-if="showAddProgramModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ editMode ? '編輯功能' : '新增功能' }}</h2>
          <button class="btn-close" @click="closeAddProgramModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveProgram">
            <div class="form-group">
              <label for="program-name">功能名稱 <span class="required">*</span></label>
              <input 
                type="text" 
                id="program-name" 
                v-model="programForm.program_name" 
                required
                placeholder="例如: 用戶管理"
              />
            </div>
            <div class="form-group">
              <label for="program-code">功能代碼 <span class="required">*</span></label>
              <input 
                type="text" 
                id="program-code" 
                v-model="programForm.program_code" 
                required
                placeholder="例如: USER_MANAGEMENT"
              />
            </div>
            <div class="form-group">
              <label for="program-url">路由URL</label>
              <input 
                type="text" 
                id="program-url" 
                v-model="programForm.program_url" 
                placeholder="例如: /admin/users"
              />
            </div>
            <div class="form-group">
              <label for="program-description">功能說明</label>
              <textarea 
                id="program-description" 
                v-model="programForm.program_description" 
                rows="3"
                placeholder="描述此功能的用途和權限範圍"
              ></textarea>
            </div>
            <div class="form-group">
              <label for="permission-type">權限類型</label>
              <select id="permission-type" v-model="programForm.permission_type">
                <option value="READ">READ (讀取)</option>
                <option value="CREATE">CREATE (新增)</option>
                <option value="UPDATE">UPDATE (更新)</option>
                <option value="DELETE">DELETE (刪除)</option>
                <option value="EXPORT">EXPORT (匯出)</option>
                <option value="IMPORT">IMPORT (匯入)</option>
                <option value="APPROVE">APPROVE (核准)</option>
                <option value="ALL">ALL (完整權限)</option>
              </select>
            </div>
            <div class="form-group">
              <label for="parent-id">父層功能</label>
              <select id="parent-id" v-model="programForm.parent_id">
                <option :value="null">無 (頂層功能)</option>
                <option 
                  v-for="program in parentProgramOptions" 
                  :key="program.id" 
                  :value="program.id"
                >
                  {{ program.program_name }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="status">狀態</label>
              <select id="status" v-model="programForm.status">
                <option :value="1">啟用</option>
                <option :value="0">停用</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeAddProgramModal">
                取消
              </button>
              <button type="submit" class="btn-primary">
                {{ editMode ? '更新' : '儲存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 模板模態框 -->
    <div class="modal" v-if="showTemplateModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ editTemplateMode ? '編輯模板' : '新增權限模板' }}</h2>
          <button class="btn-close" @click="closeTemplateModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveTemplate">
            <div class="form-group">
              <label for="template-name">模板名稱 <span class="required">*</span></label>
              <input 
                type="text" 
                id="template-name" 
                v-model="templateForm.name" 
                required
                placeholder="例如: 內容編輯權限集"
              />
            </div>
            <div class="form-group">
              <label for="template-description">模板說明</label>
              <textarea 
                id="template-description" 
                v-model="templateForm.description" 
                rows="3"
                placeholder="描述此模板的用途和適用範圍"
              ></textarea>
            </div>
            <div class="form-group">
              <label>包含的權限</label>
              <div class="template-permissions">
                <div class="permission-select-tools">
                  <button type="button" class="btn-sm" @click="expandAllTemplateGroups">全部展開</button>
                  <button type="button" class="btn-sm" @click="collapseAllTemplateGroups">全部收合</button>
                </div>
                
                <div class="permission-tree">
                  <div class="permission-category" v-for="(group, index) in permissionGroups" :key="index">
                    <div class="category-header" @click="toggleTemplateGroup(index)">
                      <i :class="['fa', expandedTemplateGroups[index] ? 'fa-minus-square' : 'fa-plus-square']"></i>
                      <span>{{ group.name }}</span>
                      <div class="category-actions">
                        <label class="toggle-all">
                          <input 
                            type="checkbox" 
                            :checked="isTemplateGroupChecked(group.programs)" 
                            :indeterminate="isTemplateGroupIndeterminate(group.programs)"
                            @change="toggleTemplateGroupPermissions(group.programs, $event)"
                          >
                          <span class="checkmark"></span>
                        </label>
                      </div>
                    </div>
                    <div class="category-items" v-if="expandedTemplateGroups[index]">
                      <div class="permission-item" v-for="program in group.programs" :key="program.id">
                        <div class="item-info">
                          <span class="item-name">{{ program.program_name }}</span>
                          <small class="permission-type">{{ program.permission_type }}</small>
                        </div>
                        <label class="toggle">
                          <input 
                            type="checkbox" 
                            :checked="isTemplatePermissionSelected(program.id)"
                            @change="toggleTemplatePermission(program.id, $event)"
                          >
                          <span class="slider"></span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeTemplateModal">
                取消
              </button>
              <button type="submit" class="btn-primary">
                {{ editTemplateMode ? '更新' : '儲存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 套用模板模態框 -->
    <div class="modal" v-if="showApplyTemplateModal">
      <div class="modal-content modal-sm">
        <div class="modal-header">
          <h2>套用權限模板</h2>
          <button class="btn-close" @click="closeApplyTemplateModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>您正在將模板「{{ selectedTemplate?.name }}」套用到角色：</p>
          <div class="form-group">
            <label for="apply-template-role">選擇角色 <span class="required">*</span></label>
            <select 
              id="apply-template-role" 
              v-model="applyTemplateRoleId" 
              required
            >
              <option value="">-- 請選擇角色 --</option>
              <option v-for="role in roles" :key="role.id" :value="role.id">{{ role.role_name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="applyTemplateOverwrite">
              <span>覆蓋現有權限</span>
            </label>
            <small class="help-text">
              開啟此選項將清除角色現有的所有權限，並僅套用模板中的權限。
              關閉則將模板權限添加到現有權限中。
            </small>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeApplyTemplateModal">
              取消
            </button>
            <button 
              type="button" 
              class="btn-primary" 
              @click="confirmApplyTemplate"
              :disabled="!applyTemplateRoleId"
            >
              套用
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模板預覽模態框 -->
    <div class="modal" v-if="showTemplatePreviewModal">
      <div class="modal-content modal-lg">
        <div class="modal-header">
          <h2>模板預覽：{{ selectedTemplate?.name }}</h2>
          <button class="btn-close" @click="closeTemplatePreviewModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p class="template-preview-desc">{{ selectedTemplate?.description }}</p>
          
          <h3 class="preview-subtitle">包含的權限</h3>
          <div class="template-preview-list">
            <div v-for="(group, groupIndex) in templatePreviewData" :key="groupIndex" class="preview-group">
              <div class="preview-group-header" @click="togglePreviewGroup(groupIndex)">
                <i :class="['fa', expandedPreviewGroups[groupIndex] ? 'fa-minus-square' : 'fa-plus-square']"></i>
                <span>{{ group.name }}</span>
                <span class="preview-count">{{ group.permissions.length }} 項</span>
              </div>
              <div class="preview-group-items" v-if="expandedPreviewGroups[groupIndex]">
                <div v-for="permission in group.permissions" :key="permission.id" class="preview-item">
                  <i class="fa fa-check-circle"></i>
                  <div class="preview-item-content">
                    <div class="preview-item-name">{{ permission.name }}</div>
                    <small class="preview-item-type">{{ permission.type }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-primary" @click="applyTemplate(selectedTemplate)">
              套用到角色
            </button>
            <button type="button" class="btn-secondary" @click="closeTemplatePreviewModal">
              關閉
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted, computed, reactive, watch } from 'vue';
import permissionApi from '../../api/permissionApi';
import { useToast } from '../../composables/useToast';
import { useAuthStore } from '../../stores/auth';
  
// 通知功能
const { showToast } = useToast();
const authStore = useAuthStore();
  
// 全域狀態
const activeTab = ref('functions');
const loading = ref(false);
const error = ref(null);

// 功能資料
const programs = ref([]);
const viewMode = ref('tree');
const searchQuery = ref('');
const expandedNodes = ref({});
const filteredPrograms = ref([]);
const vizType = ref('tree');
  
// 角色資料
const roles = ref([]);
const selectedRoleId = ref('');
const assignedPermissions = ref([]);

// 模板資料
const permissionTemplates = ref([]);
const loadingTemplates = ref(false);
const showTemplateModal = ref(false);
const showApplyTemplateModal = ref(false);
const showTemplatePreviewModal = ref(false);
const selectedTemplate = ref(null);
const editTemplateMode = ref(false);
const expandedTemplateGroups = ref({});
const selectedTemplatePermissions = ref([]);
const applyTemplateRoleId = ref('');
const applyTemplateOverwrite = ref(false);
const expandedPreviewGroups = ref({});
const templatePreviewData = ref([]);

// 審計日誌
const auditLogs = ref([]);
const loadingAudit = ref(false);
const expandedAudit = ref({});
const users = ref([]);
const auditFilters = reactive({
  startDate: new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split('T')[0],
  endDate: new Date().toISOString().split('T')[0],
  userId: '',
  actionType: ''
});

// 模態框狀態
const showAddProgramModal = ref(false);
const editMode = ref(false);
  
// 功能表單
const initialFormState = {
  program_name: '',
  program_code: '',
  program_url: '',
  program_description: '',
  permission_type: 'READ',
  parent_id: null,
  status: 1
};
  
const programForm = reactive({...initialFormState});

// 模板表單
const initialTemplateState = {
  name: '',
  description: '',
  permissions: []
};

const templateForm = reactive({...initialTemplateState});
  
// 計算屬性
const rootPrograms = computed(() => {
  return programs.value.filter(program => program.parent_id === null);
});
  
const parentProgramOptions = computed(() => {
  // 編輯模式下過濾掉自己和子功能，避免循環參照
  if (editMode.value) {
    return programs.value.filter(program => 
      program.parent_id === null && 
      program.id !== programForm.id && 
      !isDescendant(program.id, programForm.id)
    );
  }
  // 新增模式下只顯示頂層功能
  return programs.value.filter(program => program.parent_id === null);
});

// 根據父功能分組
const permissionGroups = computed(() => {
  const groups = [];
  
  // 先加入所有根功能作為組
  rootPrograms.value.forEach(program => {
    const children = getChildrenPrograms(program.id);
    groups.push({
      id: program.id,
      name: program.program_name,
      programs: [program, ...children]
    });
  });
  
  // 如果有孤立功能（沒有父節點也不是父節點的）
  const allProgramIds = programs.value.map(p => p.id);
  const includedIds = new Set();
  
  groups.forEach(group => {
    group.programs.forEach(program => {
      includedIds.add(program.id);
    });
  });
  
  // 找出未包含的功能
  const orphanPrograms = programs.value.filter(p => !includedIds.has(p.id));
  
  if (orphanPrograms.length > 0) {
    groups.push({
      id: 'orphan',
      name: '其他功能',
      programs: orphanPrograms
    });
  }
  
  return groups;
});
  
// 判斷是否為指定節點的後代
const isDescendant = (parentId, childId) => {
  const children = getChildrenPrograms(parentId);
  if (children.some(child => child.id === childId)) return true;
  
  for (const child of children) {
    if (isDescendant(child.id, childId)) return true;
  }
  return false;
};
  
// 獲取子功能
const getChildrenPrograms = (parentId) => {
  return programs.value.filter(program => program.parent_id === parentId);
};
  
// 檢查功能是否有子項目
const hasChildren = (program) => {
  return programs.value.some(p => p.parent_id === program.id);
};
  
// 獲取功能名稱
const getProgramName = (programId) => {
  const program = programs.value.find(p => p.id === programId);
  return program ? program.program_name : '';
};
  
// 切換功能展開狀態
const toggleProgram = (programId) => {
  expandedNodes.value = {
    ...expandedNodes.value,
    [programId]: !expandedNodes.value[programId]
  };
};
  
// 過濾功能
const filterPrograms = () => {
  if (!searchQuery.value) {
    filteredPrograms.value = [...programs.value];
    return;
  }
  
  const query = searchQuery.value.toLowerCase();
  filteredPrograms.value = programs.value.filter(program => 
    program.program_name.toLowerCase().includes(query) ||
    program.program_code.toLowerCase().includes(query) ||
    (program.program_url && program.program_url.toLowerCase().includes(query))
  );
};

// 日期格式化
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 日期時間格式化
const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 載入功能資料
const loadPrograms = async () => {
  loading.value = true;
  
  try {
    // 使用模擬數據進行演示
    // 在實際應用中，這裡應該調用 API
    // const response = await permissionApi.getAllPrograms();
    // programs.value = response.data;
    
    // 模擬數據
    setTimeout(() => {
      programs.value = [
        {
          id: 1,
          program_name: '使用者管理',
          program_code: 'USER_MANAGEMENT',
          program_url: '/admin/users',
          program_description: '管理系統使用者',
          permission_type: 'ALL',
          parent_id: null,
          status: 1
        },
        {
          id: 2,
          program_name: '查看使用者',
          program_code: 'USER_VIEW',
          program_url: '/admin/users/view',
          program_description: '查看使用者資訊',
          permission_type: 'READ',
          parent_id: 1,
          status: 1
        },
        {
          id: 3,
          program_name: '新增使用者',
          program_code: 'USER_CREATE',
          program_url: '/admin/users/create',
          program_description: '新增系統使用者',
          permission_type: 'CREATE',
          parent_id: 1,
          status: 1
        },
        {
          id: 4,
          program_name: '角色管理',
          program_code: 'ROLE_MANAGEMENT',
          program_url: '/admin/roles',
          program_description: '管理系統角色',
          permission_type: 'ALL',
          parent_id: null,
          status: 1
        },
        {
          id: 5,
          program_name: '露營地管理',
          program_code: 'CAMP_MANAGEMENT',
          program_url: '/admin/camps',
          program_description: '管理露營地資訊',
          permission_type: 'ALL',
          parent_id: null,
          status: 1
        },
        {
          id: 6,
          program_name: '查看露營地',
          program_code: 'CAMP_VIEW',
          program_url: '/admin/camps/view',
          program_description: '查看露營地資訊',
          permission_type: 'READ',
          parent_id: 5,
          status: 1
        },
        {
          id: 7,
          program_name: '編輯露營地',
          program_code: 'CAMP_EDIT',
          program_url: '/admin/camps/edit',
          program_description: '編輯露營地資訊',
          permission_type: 'UPDATE',
          parent_id: 5,
          status: 1
        }
      ];
      
      filteredPrograms.value = [...programs.value];
      
      // 預設展開所有根節點
      const expandedState = {};
      rootPrograms.value.forEach(program => {
        expandedState[program.id] = true;
      });
      expandedNodes.value = expandedState;
      
      loading.value = false;
    }, 500);
    
  } catch (err) {
    error.value = err.message || '載入功能資料失敗';
    showToast(error.value, 'error');
    loading.value = false;
  }
};

// 載入角色資料
const loadRoles = async () => {
  try {
    // 在實際應用中應該調用 API
    // const response = await permissionApi.getAllRoles();
    // roles.value = response.data;
    
    // 模擬數據
    setTimeout(() => {
      roles.value = [
        { id: 1, role_name: '超級管理員', role_code: 'ROLE_SUPER_ADMIN', status: 1 },
        { id: 2, role_name: '一般管理員', role_code: 'ROLE_ADMIN', status: 1 },
        { id: 3, role_name: '營地管理員', role_code: 'ROLE_CAMP_OWNER', status: 1 },
        { id: 4, role_name: '會員', role_code: 'ROLE_USER', status: 1 }
      ];
    }, 300);
  } catch (err) {
    showToast(err.message || '載入角色資料失敗', 'error');
  }
};

// 載入權限模板
const loadPermissionTemplates = async () => {
  loadingTemplates.value = true;
  
  try {
    // 在實際應用中應該調用 API
    // const response = await permissionApi.getAllTemplates();
    // permissionTemplates.value = response.data;
    
    // 模擬數據
    setTimeout(() => {
      permissionTemplates.value = [
        {
          id: 1,
          name: '基礎使用者管理',
          description: '包含基本的使用者查看與編輯權限',
          permissions_count: 3,
          is_system: true,
          created_at: '2023-10-01T10:30:00Z',
          permissions: [1, 2, 3]
        },
        {
          id: 2,
          name: '露營地管理',
          description: '適用於營地管理員的權限集',
          permissions_count: 3,
          is_system: true,
          created_at: '2023-10-05T14:20:00Z',
          permissions: [5, 6, 7]
        },
        {
          id: 3,
          name: '一般管理員',
          description: '一般管理員的完整權限集',
          permissions_count: 5,
          is_system: false,
          created_at: '2023-10-10T09:15:00Z',
          permissions: [1, 2, 4, 5, 6]
        }
      ];
      
      loadingTemplates.value = false;
    }, 500);
  } catch (err) {
    showToast(err.message || '載入權限模板失敗', 'error');
    loadingTemplates.value = false;
  }
};

// 載入審計日誌
const loadAuditLogs = async () => {
  loadingAudit.value = true;
  
  try {
    // 在實際應用中應該調用 API
    // const response = await permissionApi.getAuditLogs(auditFilters);
    // auditLogs.value = response.data;
    
    // 模擬數據
    setTimeout(() => {
      auditLogs.value = [
        {
          id: 1,
          user_id: 1,
          user_name: '系統管理員',
          action_type: 'CREATE',
          target_type: 'PROGRAM',
          target_id: 7,
          message: '創建了新功能「編輯露營地」',
          details: JSON.stringify({
            program_name: '編輯露營地',
            program_code: 'CAMP_EDIT',
            permission_type: 'UPDATE'
          }),
          created_at: '2023-10-15T09:30:00Z'
        },
        {
          id: 2,
          user_id: 1,
          user_name: '系統管理員',
          action_type: 'ASSIGN',
          target_type: 'ROLE',
          target_id: 3,
          message: '將權限「露營地管理」分配給角色「營地管理員」',
          details: JSON.stringify({
            role_name: '營地管理員',
            permissions: ['查看露營地', '編輯露營地']
          }),
          created_at: '2023-10-15T09:35:00Z'
        },
        {
          id: 3,
          user_id: 2,
          user_name: '測試管理員',
          action_type: 'UPDATE',
          target_type: 'PROGRAM',
          target_id: 1,
          message: '更新了功能「使用者管理」',
          details: JSON.stringify({
            before: {
              program_name: '用戶管理',
              program_description: '管理系統用戶'
            },
            after: {
              program_name: '使用者管理',
              program_description: '管理系統使用者'
            }
          }),
          created_at: '2023-10-16T14:20:00Z'
        },
        {
          id: 4,
          user_id: 1,
          user_name: '系統管理員',
          action_type: 'DELETE',
          target_type: 'TEMPLATE',
          target_id: 5,
          message: '刪除了權限模板「過期模板」',
          created_at: '2023-10-17T11:05:00Z'
        }
      ];
      
      loadingAudit.value = false;
    }, 500);
    
    // 載入用戶數據用於過濾
    loadUsers();
  } catch (err) {
    showToast(err.message || '載入審計日誌失敗', 'error');
    loadingAudit.value = false;
  }
};

// 載入用戶數據
const loadUsers = async () => {
  try {
    // 在實際應用中應該調用 API
    // const response = await permissionApi.getAllUsers();
    // users.value = response.data;
    
    // 模擬數據
    setTimeout(() => {
      users.value = [
        { id: 1, username: '系統管理員' },
        { id: 2, username: '測試管理員' },
        { id: 3, username: '營地編輯員' }
      ];
    }, 300);
  } catch (err) {
    showToast(err.message || '載入用戶數據失敗', 'error');
  }
};

// 獲取操作類型標籤
const getActionLabel = (actionType) => {
  const actionMap = {
    'CREATE': '新增',
    'UPDATE': '更新',
    'DELETE': '刪除',
    'ASSIGN': '權限分配'
  };
  return actionMap[actionType] || actionType;
};

// 獲取操作類型樣式類
const getActionClass = (actionType) => {
  const classMap = {
    'CREATE': 'create',
    'UPDATE': 'update',
    'DELETE': 'delete',
    'ASSIGN': 'assign'
  };
  return classMap[actionType] || '';
};

// 格式化審計詳情
const formatAuditDetails = (details) => {
  if (!details) return '';
  
  try {
    const detailsObj = JSON.parse(details);
    return JSON.stringify(detailsObj, null, 2);
  } catch (e) {
    return details;
  }
};

// 切換審計詳情顯示
const toggleAuditDetails = (index) => {
  expandedAudit.value = {
    ...expandedAudit.value,
    [index]: !expandedAudit.value[index]
  };
};

// 刷新審計日誌
const refreshAuditLogs = () => {
  loadAuditLogs();
};

// 匯出審計日誌
const exportAuditLogs = () => {
  showToast('審計日誌匯出功能尚未實現', 'info');
};

// 搜尋審計日誌
const searchAuditLogs = () => {
  loadAuditLogs();
};

// 添加子功能
const addChildProgram = (parentProgram) => {
  resetForm();
  editMode.value = false;
  programForm.parent_id = parentProgram.id;
  showAddProgramModal.value = true;
};
  
// 編輯功能
const editProgram = (program) => {
  resetForm();
  editMode.value = true;
  Object.assign(programForm, program);
  showAddProgramModal.value = true;
};
  
// 刪除功能
const deleteProgram = async (program) => {
  // 檢查是否有子功能
  if (hasChildren(program)) {
    showToast('無法刪除含有子功能的項目，請先刪除所有子功能', 'error');
    return;
  }
  
  if (!confirm(`確定要刪除功能 "${program.program_name}" 嗎？`)) {
    return;
  }
  
  try {
    // 在實際應用中應調用 API
    // await permissionApi.deleteProgram(program.id);
    
    // 模擬刪除操作
    programs.value = programs.value.filter(p => p.id !== program.id);
    filteredPrograms.value = filteredPrograms.value.filter(p => p.id !== program.id);
    
    showToast('功能已刪除', 'success');
  } catch (err) {
    showToast(err.response?.data?.message || '刪除功能失敗', 'error');
  }
};
  
// 儲存功能
const saveProgram = async () => {
  try {
    if (editMode.value) {
      // 在實際應用中應調用 API
      // await permissionApi.updateProgram(programForm.id, programForm);
      
      // 模擬更新操作
      const index = programs.value.findIndex(p => p.id === programForm.id);
      if (index !== -1) {
        programs.value[index] = { ...programForm };
      }
      
      showToast('功能已更新', 'success');
    } else {
      // 在實際應用中應調用 API
      // const response = await permissionApi.createProgram(programForm);
      
      // 模擬創建操作
      const newProgram = {
        ...programForm,
        id: Math.max(...programs.value.map(p => p.id), 0) + 1
      };
      programs.value.push(newProgram);
      
      showToast('功能已建立', 'success');
    }
    
    // 重新過濾結果
    filterPrograms();
    closeAddProgramModal();
  } catch (err) {
    showToast(err.response?.data?.message || '儲存功能失敗', 'error');
  }
};
  
// 重置表單
const resetForm = () => {
  Object.assign(programForm, initialFormState);
};
  
// 關閉添加功能對話框
const closeAddProgramModal = () => {
  showAddProgramModal.value = false;
  resetForm();
  editMode.value = false;
};

// 判斷權限是否已分配
const isPermissionAssigned = (programId) => {
  return assignedPermissions.value.includes(programId);
};

// 切換模板分組展開
const toggleTemplateGroup = (index) => {
  expandedTemplateGroups.value = {
    ...expandedTemplateGroups.value,
    [index]: !expandedTemplateGroups.value[index]
  };
};

// 展開所有模板分組
const expandAllTemplateGroups = () => {
  const expanded = {};
  permissionGroups.value.forEach((_, index) => {
    expanded[index] = true;
  });
  expandedTemplateGroups.value = expanded;
};

// 收合所有模板分組
const collapseAllTemplateGroups = () => {
  expandedTemplateGroups.value = {};
};

// 判斷模板權限是否選中
const isTemplatePermissionSelected = (programId) => {
  return selectedTemplatePermissions.value.includes(programId);
};

// 切換模板權限選擇
const toggleTemplatePermission = (programId, event) => {
  const isChecked = event.target.checked;
  
  if (isChecked) {
    if (!selectedTemplatePermissions.value.includes(programId)) {
      selectedTemplatePermissions.value.push(programId);
    }
  } else {
    selectedTemplatePermissions.value = selectedTemplatePermissions.value.filter(id => id !== programId);
  }
};

// 檢查模板分組是否全選
const isTemplateGroupChecked = (programs) => {
  return programs.length > 0 && programs.every(program => isTemplatePermissionSelected(program.id));
};

// 檢查模板分組是否部分選中
const isTemplateGroupIndeterminate = (programs) => {
  const selectedCount = programs.filter(program => isTemplatePermissionSelected(program.id)).length;
  return selectedCount > 0 && selectedCount < programs.length;
};

// 切換模板分組權限
const toggleTemplateGroupPermissions = (programs, event) => {
  const isChecked = event.target.checked;
  
  programs.forEach(program => {
    if (isChecked) {
      if (!selectedTemplatePermissions.value.includes(program.id)) {
        selectedTemplatePermissions.value.push(program.id);
      }
    } else {
      selectedTemplatePermissions.value = selectedTemplatePermissions.value.filter(id => id !== program.id);
    }
  });
};

// 關閉模板模態框
const closeTemplateModal = () => {
  showTemplateModal.value = false;
  Object.assign(templateForm, initialTemplateState);
  selectedTemplatePermissions.value = [];
  editTemplateMode.value = false;
};

// 儲存模板
const saveTemplate = async () => {
  // 檢查表單
  if (!templateForm.name) {
    showToast('請輸入模板名稱', 'warning');
    return;
  }
  
  if (selectedTemplatePermissions.value.length === 0) {
    showToast('請至少選擇一個權限', 'warning');
    return;
  }
  
  templateForm.permissions = [...selectedTemplatePermissions.value];
  
  try {
    // 在實際應用中應調用 API
    // const response = await permissionApi.saveTemplate(templateForm);
    
    // 模擬儲存操作
    if (editTemplateMode.value) {
      const index = permissionTemplates.value.findIndex(t => t.id === templateForm.id);
      if (index !== -1) {
        permissionTemplates.value[index] = {
          ...templateForm,
          permissions_count: templateForm.permissions.length,
          created_at: permissionTemplates.value[index].created_at
        };
      }
      
      showToast('模板已更新', 'success');
    } else {
      const newTemplate = {
        id: Math.max(...permissionTemplates.value.map(t => t.id), 0) + 1,
        name: templateForm.name,
        description: templateForm.description,
        permissions: [...templateForm.permissions],
        permissions_count: templateForm.permissions.length,
        is_system: false,
        created_at: new Date().toISOString()
      };
      
      permissionTemplates.value.push(newTemplate);
      showToast('模板已建立', 'success');
    }
    
    closeTemplateModal();
  } catch (err) {
    showToast(err.message || '儲存模板失敗', 'error');
  }
};

// 預覽模板
const previewTemplate = (template) => {
  selectedTemplate.value = template;
  
  // 構建預覽數據
  templatePreviewData.value = [];
  
  // 按照模板權限組織數據
  const groupMap = new Map();
  
  permissionGroups.value.forEach(group => {
    const groupPermissions = group.programs.filter(program => 
      template.permissions.includes(program.id)
    );
    
    if (groupPermissions.length > 0) {
      templatePreviewData.value.push({
        name: group.name,
        permissions: groupPermissions.map(p => ({
          id: p.id,
          name: p.program_name,
          type: p.permission_type
        }))
      });
    }
  });
  
  // 預設展開所有分組
  const previewExpanded = {};
  templatePreviewData.value.forEach((_, index) => {
    previewExpanded[index] = true;
  });
  expandedPreviewGroups.value = previewExpanded;
  
  showTemplatePreviewModal.value = true;
};

// 關閉模板預覽
const closeTemplatePreviewModal = () => {
  showTemplatePreviewModal.value = false;
  selectedTemplate.value = null;
  templatePreviewData.value = [];
};

// 套用模板
const applyTemplate = (template) => {
  selectedTemplate.value = template;
  applyTemplateRoleId.value = '';
  applyTemplateOverwrite.value = false;
  showApplyTemplateModal.value = true;
};

// 關閉套用模板
const closeApplyTemplateModal = () => {
  showApplyTemplateModal.value = false;
  selectedTemplate.value = null;
  applyTemplateRoleId.value = '';
};

// 確認套用模板
const confirmApplyTemplate = async () => {
  if (!applyTemplateRoleId.value) {
    showToast('請選擇角色', 'warning');
    return;
  }
  
  try {
    // 在實際應用中應調用 API
    // await permissionApi.applyTemplateToRole(selectedTemplate.value.id, applyTemplateRoleId.value, applyTemplateOverwrite.value);
    
    // 模擬套用操作
    setTimeout(() => {
      showToast(`模板「${selectedTemplate.value.name}」已成功套用到所選角色`, 'success');
      closeApplyTemplateModal();
    }, 500);
  } catch (err) {
    showToast(err.message || '套用模板失敗', 'error');
  }
};

// 刪除模板
const deleteTemplate = async (template) => {
  if (!confirm(`確定要刪除模板「${template.name}」嗎？`)) {
    return;
  }
  
  try {
    // 在實際應用中應調用 API
    // await permissionApi.deleteTemplate(template.id);
    
    // 模擬刪除操作
    permissionTemplates.value = permissionTemplates.value.filter(t => t.id !== template.id);
    showToast('模板已刪除', 'success');
  } catch (err) {
    showToast(err.message || '刪除模板失敗', 'error');
  }
};

// 切換預覽分組
const togglePreviewGroup = (index) => {
  expandedPreviewGroups.value = {
    ...expandedPreviewGroups.value,
    [index]: !expandedPreviewGroups.value[index]
  };
};

// 初始化
onMounted(async () => {
  await loadPrograms();
  await loadRoles();
  await loadPermissionTemplates();
  loadAuditLogs();
  
  // 初始化模板分組展開狀態
  const groupState = {};
  permissionGroups.value.forEach((_, index) => {
    groupState[index] = index < 3; // 只展開前三個分組
  });
  expandedTemplateGroups.value = groupState;
});

// 當選擇的標籤頁變化時的處理
watch(activeTab, (newTab) => {
  if (newTab === 'templates' && permissionTemplates.value.length === 0) {
    loadPermissionTemplates();
  } else if (newTab === 'audit' && auditLogs.value.length === 0) {
    loadAuditLogs();
  }
});
</script>
  
<style scoped>
.permission-management {
  width: 100%;
  padding: 20px;
}

/* 標籤頁樣式 */
.tabs-container {
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 20px;
}

.tab {
  padding: 12px 20px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-weight: 500;
  color: var(--gray-600);
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.tab:hover {
  color: var(--tiffany-blue);
}

.tab.active {
  color: var(--tiffany-blue);
  border-bottom-color: var(--tiffany-blue);
}

.tab i {
  font-size: 16px;
}

.tab-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #1e293b;
}

/* 工具列 */
.tree-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 8px 12px;
  padding-right: 40px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.search-box i {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
}

.view-options {
  display: flex;
  gap: 10px;
}

.view-option {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.view-option.active {
  background-color: var(--tiffany-bg);
  border-color: var(--tiffany-blue);
  color: var(--tiffany-blue);
}

/* 樹狀檢視 */
.tree-view {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 15px;
}

.tree-item {
  margin-bottom: 5px;
}

.tree-item-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.tree-item-header:hover {
  background-color: var(--tiffany-bg);
}

.tree-item-header i {
  margin-right: 10px;
  color: #64748b;
  width: 20px;
  text-align: center;
}

.tree-item-name {
  flex: 1;
  font-weight: 500;
}

.tree-item-actions {
  display: flex;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.3s;
}

.tree-item-header:hover .tree-item-actions {
  opacity: 1;
}

.tree-item-children {
  margin-left: 25px;
  padding-left: 10px;
  border-left: 1px dashed #e2e8f0;
}

.tree-child-item {
  margin-top: 5px;
}

.permission-type {
  color: var(--gray-600);
  margin-right: 10px;
  font-size: 12px;
  padding: 2px 6px;
  background-color: var(--tiffany-bg);
  border-radius: 4px;
}

/* 列表檢視 */
.list-view {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow-x: auto;
}

.programs-table {
  width: 100%;
  border-collapse: collapse;
}

.programs-table th,
.programs-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.programs-table th {
  background-color: var(--tiffany-bg);
  font-weight: 600;
  color: var(--gray-700);
}

.programs-table tr:hover {
  background-color: var(--tiffany-bg);
}

.status-badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 50px;
  font-size: 12px;
}

.status-active {
  background-color: #dcfce7;
  color: #14532d;
}

.status-disabled {
  background-color: #fee2e2;
  color: #7f1d1d;
}

.actions {
  display: flex;
  gap: 5px;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  color: #64748b;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.btn-icon:hover {
  background-color: var(--tiffany-bg);
  color: var(--tiffany-blue);
}

.btn-icon:disabled {
  color: #cbd5e1;
  cursor: not-allowed;
}

/* 視覺化檢視 */
.visual-view {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 15px;
}

.viz-toolbar {
  display: flex;
  margin-bottom: 15px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 10px;
}

.viz-option {
  padding: 8px 16px;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  margin-right: 10px;
  font-weight: 500;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 5px;
}

.viz-option:hover {
  background-color: var(--tiffany-bg);
}

.viz-option.active {
  background-color: var(--tiffany-blue);
  color: white;
}

.visual-graph-container {
  height: 500px;
  border: 1px dashed #e2e8f0;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-placeholder {
  text-align: center;
  color: #94a3b8;
}

.visual-placeholder i {
  font-size: 48px;
  margin-bottom: 10px;
}

.visual-placeholder small {
  display: block;
  margin-top: 10px;
  font-size: 12px;
}

/* 權限模板 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.template-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.template-card:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.template-header h3 {
  font-size: 18px;
  color: #1e293b;
  margin: 0;
}

.template-badge {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 50px;
}

.template-badge.system {
  background-color: var(--tiffany-bg);
  color: var(--tiffany-blue-dark);
}

.template-badge.custom {
  background-color: #f8fafc;
  color: #64748b;
}

.template-desc {
  color: #64748b;
  margin-bottom: 15px;
  min-height: 40px;
}

.template-meta {
  display: flex;
  justify-content: space-between;
  color: #94a3b8;
  font-size: 12px;
  margin-bottom: 15px;
}

.template-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.template-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.add-template {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--tiffany-bg);
  border: 1px dashed var(--tiffany-blue);
}

.add-template:hover {
  background-color: var(--tiffany-bg);
}

.add-template-content {
  text-align: center;
  color: var(--tiffany-blue);
}

.add-template-content i {
  font-size: 36px;
  margin-bottom: 10px;
  color: var(--tiffany-blue);
}

/* 審計日誌 */
.header-actions {
  display: flex;
  gap: 10px;
}

.audit-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: var(--tiffany-bg);
  border-radius: 8px;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.filter-group label {
  font-weight: 500;
  font-size: 14px;
  color: #475569;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.audit-filters select {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  min-width: 150px;
}

.audit-timeline {
  position: relative;
  margin-left: 30px;
  padding-left: 30px;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
}

.timeline-point {
  position: absolute;
  left: -40px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #94a3b8;
  border: 3px solid white;
  box-shadow: 0 0 0 3px #f1f5f9;
}

.timeline-point.create {
  background-color: var(--tiffany-blue);
}

.timeline-point.update {
  background-color: var(--tiffany-blue-dark);
}

.timeline-point.delete {
  background-color: #ef4444;
}

.timeline-point.assign {
  background-color: var(--tiffany-blue-accent);
}

.timeline-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 15px;
  position: relative;
}

.timeline-content::before {
  content: '';
  position: absolute;
  left: -20px;
  top: 15px;
  border: 10px solid transparent;
  border-right-color: white;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.timeline-time {
  color: #64748b;
  font-size: 14px;
}

.action-badge {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 50px;
  color: white;
}

.action-badge.create {
  background-color: var(--tiffany-blue);
}

.action-badge.update {
  background-color: var(--tiffany-blue-dark);
}

.action-badge.delete {
  background-color: #ef4444;
}

.action-badge.assign {
  background-color: var(--tiffany-blue-accent);
}

.timeline-body {
  color: #334155;
}

.user-info {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.audit-message {
  margin-bottom: 10px;
}

.audit-details {
  margin-top: 10px;
}

.details-toggle {
  cursor: pointer;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  margin-bottom: 5px;
}

.details-content {
  background-color: var(--tiffany-bg);
  border-radius: 4px;
  padding: 10px;
  overflow-x: auto;
}

.details-content pre {
  margin: 0;
  font-family: monospace;
  font-size: 12px;
  color: #475569;
}

/* 按鈕 */
.btn-primary {
  background-color: var(--tiffany-blue);
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-primary:hover {
  background-color: var(--tiffany-blue-dark);
}

.btn-secondary {
  background-color: #e2e8f0;
  color: #334155;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary:hover {
  background-color: #cbd5e1;
}

.btn-outline {
  background-color: white;
  color: #475569;
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-outline:hover {
  background-color: var(--tiffany-bg);
  color: var(--tiffany-blue);
  border-color: var(--tiffany-blue);
}

.btn-sm {
  padding: 5px 10px;
  font-size: 12px;
}

.btn-danger,
.btn-danger:hover {
  color: #ef4444;
}

/* 載入與空狀態 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.loading-state i,
.empty-state i {
  font-size: 48px;
  color: #94a3b8;
  margin-bottom: 15px;
}

.empty-state p {
  color: #64748b;
  margin-bottom: 20px;
}

/* 表單樣式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content.modal-sm {
  max-width: 400px;
}

.modal-content.modal-lg {
  max-width: 700px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  font-size: 18px;
  color: #1e293b;
}

.btn-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #64748b;
  font-size: 18px;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #475569;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  background-color: white;
}

.form-group small {
  display: block;
  margin-top: 5px;
  color: #64748b;
  font-size: 12px;
}

.help-text {
  display: block;
  margin-top: 5px;
  color: #64748b;
  font-size: 12px;
}

.required {
  color: #ef4444;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* 選擇框樣式 */
.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input {
  margin-right: 8px;
  width: auto;
}

/* 開關按鈕樣式 */
.toggle {
  position: relative;
  display: inline-block;
  width: 42px;
  height: 22px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #e2e8f0;
  transition: .4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--tiffany-blue);
}

input:checked + .slider:before {
  transform: translateX(20px);
}

input:disabled + .slider {
  background-color: #cbd5e1;
  cursor: not-allowed;
}

/* 權限選擇樹樣式 */
.template-permissions {
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}

.permission-select-tools {
  padding: 10px;
  display: flex;
  gap: 10px;
  border-bottom: 1px solid #e2e8f0;
}

.permission-tree {
  padding: 10px;
}

/* 模板預覽樣式 */
.template-preview-desc {
  margin-bottom: 20px;
  color: #64748b;
}

.preview-subtitle {
  font-size: 16px;
  margin-bottom: 15px;
  color: #334155;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 8px;
}

.preview-group {
  margin-bottom: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.preview-group-header {
  padding: 10px 15px;
  background-color: var(--tiffany-bg);
  display: flex;
  align-items: center;
  cursor: pointer;
}

.preview-group-header i {
  margin-right: 10px;
  color: #64748b;
  width: 16px;
  text-align: center;
}

.preview-group-header span {
  flex: 1;
  font-weight: 500;
}

.preview-count {
  color: #64748b;
  font-size: 12px;
  background-color: rgba(129, 216, 208, 0.2);
  padding: 2px 6px;
  border-radius: 10px;
}

.preview-group-items {
  padding: 10px 15px;
}

.preview-item {
  display: flex;
  align-items: center;
  padding: 5px 0;
  border-bottom: 1px dashed #f1f5f9;
}

.preview-item:last-child {
  border-bottom: none;
}

.preview-item i {
  color: var(--tiffany-blue);
  margin-right: 10px;
}

.preview-item-content {
  flex: 1;
}

.preview-item-name {
  font-weight: 500;
}

.preview-item-type {
  color: #64748b;
  font-size: 12px;
}

/* 權限分組樣式 */
.permission-category {
  margin-bottom: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.category-header {
  padding: 12px 15px;
  background-color: var(--tiffany-bg);
  display: flex;
  align-items: center;
  cursor: pointer;
}

.category-header:hover {
  background-color: rgba(129, 216, 208, 0.2);
}

.category-header i {
  margin-right: 10px;
  color: var(--tiffany-blue-dark);
  width: 16px;
  text-align: center;
}

.category-header span {
  flex: 1;
  font-weight: 500;
  color: var(--gray-700);
}

.category-actions {
  display: flex;
  align-items: center;
}

.category-items {
  border-top: 1px solid #e2e8f0;
}

.permission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #f1f5f9;
}

.permission-item:last-child {
  border-bottom: none;
}

.permission-item:hover {
  background-color: var(--tiffany-bg);
}

.item-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-name {
  font-weight: 400;
}

/* 響應式樣式 */
@media (max-width: 768px) {
  .tree-toolbar,
  .matrix-tools {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .template-grid {
    grid-template-columns: 1fr;
  }
  
  .audit-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-range {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tab {
    padding: 8px 10px;
    font-size: 14px;
  }
}

.checkmark {
  position: relative;
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid #e2e8f0;
  border-radius: 3px;
  margin-right: 5px;
}

input:checked ~ .checkmark:after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

input:checked ~ .checkmark {
  background-color: var(--tiffany-blue);
  border-color: var(--tiffany-blue);
}

input:indeterminate ~ .checkmark {
  background-color: var(--tiffany-blue-light);
  border-color: var(--tiffany-blue);
}

input:indeterminate ~ .checkmark:after {
  content: '';
  position: absolute;
  left: 3px;
  top: 7px;
  width: 8px;
  height: 2px;
  background-color: white;
}

.toggle-all {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.toggle-all input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}
</style>