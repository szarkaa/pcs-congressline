(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$stateParams', '$uibModalInstance', 'entity', 'User', 'JhiLanguageService', 'Congress'];

    function UserManagementDialogController ($stateParams, $uibModalInstance, entity, User, JhiLanguageService, Congress) {
        var vm = this;

        vm.authorities = ['ROLE_ACCOUNTANT', 'ROLE_ADVANCED_USER', 'ROLE_USER', 'ROLE_ADMIN', ''];
        vm.languages = null;
        vm.congresses = Congress.queryStripped();
        vm.user = {
            id: entity.id, login: entity.login, firstName: entity.firstName, lastName: entity.lastName, email: entity.email,
            activated: entity.activated, langKey: 'en', createdBy: entity.createdBy, createdDate: entity.createdDate,
            lastModifiedBy: entity.lastModifiedBy, lastModifiedDate: entity.lastModifiedDate,
            authorities: entity.authorities, congresses: entity.congresses
        };

        vm.save = save;
        vm.clear = clear;

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            //vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            vm.user.congressIds = vm.user.congresses ? vm.user.congresses.map(c => c.id) : [];
            delete vm.user.congresses;
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else {
                User.save(vm.user, onSaveSuccess, onSaveError);
            }
        }
    }
})();
