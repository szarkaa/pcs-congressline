(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('minimalizaSidebar', minimalizaSidebar)
        .directive('sideNavigation', sideNavigation);

    //minimalizaSidebar.$inject = ['$timeout'];
    sideNavigation.$inject = ['$timeout'];

    function minimalizaSidebar() {
        var directive = {
            restrict: 'A',
            template: '<a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="" ng-click="vm.minimalize()"><i class="fa fa-bars"></i></a>',
            controller: MinSidebarController,
            controllerAs: 'vm',
            bindToController: true
        };

        return directive;
    }

    function MinSidebarController () {
        var vm = this;
        vm.minimalize = minimalize;

        function minimalize () {
            $("body").toggleClass("mini-navbar");
            if (!$('body').hasClass('mini-navbar') || $('body').hasClass('body-small')) {
                // Hide menu in order to smoothly turn on when maximize menu
                $('#side-menu').hide();
                // For smoothly turn on menu
                setTimeout(
                    function () {
                        $('#side-menu').fadeIn(400);
                    }, 200);
            } else if ($('body').hasClass('fixed-sidebar')){
                $('#side-menu').hide();
                setTimeout(
                    function () {
                        $('#side-menu').fadeIn(400);
                    }, 100);
            } else {
                // Remove all inline style from jquery fadeIn function to reset menu state
                $('#side-menu').removeAttr('style');
            }
        }
    }

    function sideNavigation($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                // Call the metsiMenu plugin and plug it to sidebar navigation
                $timeout(function(){
                    element.metisMenu();

                });

                // Enable initial fixed sidebar
                //var sidebar = element.parent();
                //sidebar.slimScroll({
                //    height: '100%',
                //    railOpacity: 0.9,
                //});
            }
        };
    }
})();
