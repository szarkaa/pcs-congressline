(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('GroupDiscountItemFilter', GroupDiscountItemFilter);

    GroupDiscountItemFilter.$inject = ['$cookies', '$state'];

    function GroupDiscountItemFilter ($cookies, $state) {
        var groupDiscountItemFilter = null;

        return {
            setGroupDiscountItemFilter: function (filter) {
                if (filter) {
                    groupDiscountItemFilter = {
                        payingGroup: filter.payingGroup,
                        chargeableItemType: filter.chargeableItemType,
                        selectedChargeableItemIds: {}
                    };
                    $cookies.putObject('group-discount-item-filter', groupDiscountItemFilter);
                }
                else {
                    groupDiscountItemFilter = null;
                    $cookies.remove('group-discount-item-filter');
                }

            },
            getGroupDiscountItemFilter: function () {
                if (groupDiscountItemFilter == null) {
                    groupDiscountItemFilter = $cookies.getObject('group-discount-item-filter');
                    if (!groupDiscountItemFilter) {
                        return {
                            payingGroup: null,
                            chargeableItemType: null,
                            selectedChargeableItemIds: {}
                        };
                    }
                }
                return groupDiscountItemFilter;
            },
            resetSelectedItems: function () {
                if (groupDiscountItemFilter != null) {
                    groupDiscountItemFilter.selectedChargeableItemIds = {};
                }
            }
        };
    }
})();
