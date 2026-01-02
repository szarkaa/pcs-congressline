(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegController', BackendOnlineRegController);

    BackendOnlineRegController.$inject = ['$scope', '$state', '$http', 'BackendOnlineReg', 'CongressSelector', 'onlineRegFilter', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function BackendOnlineRegController ($scope, $state, $http, BackendOnlineReg, CongressSelector, onlineRegFilter, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [9, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable(),
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.onlineRegFilter = onlineRegFilter;
        vm.allOnlineRegIdSelected = false;
        vm.toggleOnlineRegSelection = toggleOnlineRegSelection;
        vm.toggleAllOnlineRegSelection = toggleAllOnlineRegSelection;
        vm.isAnyOnlineRegIdSelected = isAnyOnlineRegIdSelected;

        vm.backendOnlineRegs = [];
        vm.downloadAllSelected = downloadAllSelected;
        vm.downloadPdf = downloadPdf;

        loadAll();

        function loadAll() {
            BackendOnlineReg.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.backendOnlineRegs = result;
            });
        }

        function downloadPdf(id) {
            $http.get('/api/registration/online/' + id + '/pdf', {responseType: 'arraybuffer'})
                .success(function (response) {
                    var blob = new Blob([response], {type: 'application/pdf'});
                    var pdfLink = (window.URL || window.webkitURL).createObjectURL(blob);
                    window.open(pdfLink, '_blank');
                });
        }

        function downloadAllSelected() {
            $http.post('api/backend-online-regs/pdf/all', createOnlineRegVM(), {responseType: 'arraybuffer'})
                .success(function(data, status, headers) {
                    var filename = headers('Content-Disposition');
                    filename = filename.split('; filename=')[1].trim();
                    var blob = new Blob([data], {type: 'application/pdf'});
                    var pdfLink = (window.URL || window.webkitURL).createObjectURL(blob);
                    window.open(pdfLink, '_blank');
                });
        }

        function toggleOnlineRegSelection(onlineRegId) {
            // vm.onlineRegFilter.selectedOnlineRegIds[onlineRegId.toString()] = !isOnlineRegIdSelected(onlineRegId);
        }

        function toggleAllOnlineRegSelection() {
            if (!vm.allOnlineRegIdSelected) {
                vm.onlineRegFilter.selectedOnlineRegIds = {};
            }
            else {
                vm.onlineRegFilter.selectedOnlineRegIds = {};
                for (var i = 0; i < vm.backendOnlineRegs.length; i++) {
                    vm.onlineRegFilter.selectedOnlineRegIds[vm.backendOnlineRegs[i].id.toString()] = true;
                }
            }
        }

        function isOnlineRegIdSelected(onlineRegId) {
            for (var prop in vm.onlineRegFilter.selectedOnlineRegIds) {
                if (vm.onlineRegFilter.selectedOnlineRegIds.hasOwnProperty(prop) && prop === onlineRegId) {
                    return true;
                }
            }
            return false;
        }

        function isAnyOnlineRegIdSelected() {
            for (var prop in vm.onlineRegFilter.selectedOnlineRegIds) {
                if (vm.onlineRegFilter.selectedOnlineRegIds.hasOwnProperty(prop) && vm.onlineRegFilter.selectedOnlineRegIds[prop]) {
                    return true;
                }
            }
            return false;
        }

        function createOnlineRegVM() {
            var onlineRegVM = {};
            onlineRegVM.onlineRegIdList = [];
            for (var prop in vm.onlineRegFilter.selectedOnlineRegIds) {
                if (vm.onlineRegFilter.selectedOnlineRegIds.hasOwnProperty(prop) && vm.onlineRegFilter.selectedOnlineRegIds[prop]) {
                    onlineRegVM.onlineRegIdList.push(parseInt(prop, 10));
                }
            }
            return onlineRegVM;
        }
    }
})();
