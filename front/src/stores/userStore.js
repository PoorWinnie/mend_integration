import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        currentUser: {
            id: null,
            name: '',
            roles: [],
            // 其他欄位...
        }
    }),
    actions: {
        setCurrentUser(user) {
            this.currentUser = user;
        },
        clearCurrentUser() {
            this.currentUser = {
                id: null,
                name: '',
                roles: [],
            };
        }
    },
    persist: true
});
