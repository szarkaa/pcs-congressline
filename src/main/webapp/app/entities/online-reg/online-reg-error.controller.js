(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegErrorController', OnlineRegErrorController);

    OnlineRegErrorController.$inject = ['$timeout', '$filter', '$scope', '$state', '$stateParams', '$translate', 'tmhDynamicLocale', 'congress'];

    function OnlineRegErrorController ($timeout, $filter, $scope, $state, $stateParams, $translate, tmhDynamicLocale, congress) {
        var vm = this;
        vm.errorId = $stateParams.errorId ? $stateParams.errorId : 'unknown-error';
        vm.language = $stateParams.language;
        vm.congress = congress;

        if (vm.language) {
            $translate.use(vm.language);
            tmhDynamicLocale.set(vm.language);
        } else if (vm.congress) {
            vm.config = vm.congress.onlineRegConfig;
            $translate.use(vm.config.defaultLanguage);
            tmhDynamicLocale.set(vm.config.defaultLanguage);
        }

        vm.headerStyle = headerStyle;

        function headerStyle() {
            return vm.config && vm.config.colorCode ? {'color': '#ffffff', 'background-color': vm.config.colorCode } : {};
        }

    }
})();
