(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CompanyController', CompanyController);

    CompanyController.$inject = ['entity', 'Company'];

    function CompanyController (entity, Company) {
        var vm = this;
        vm.company = entity;
        vm.success = null;
        vm.error = null;

        vm.save = save;

        function save() {
            Company.update(vm.company, function (data) {
                vm.error = null;
                vm.success = 'OK';
            }, function (err) {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
