angular.module('pcsApp')
    .filter('filterChargeableItemsByCurrency', function () {
        return filterChargeableItemsByCurrency;
    });

function filterChargeableItemsByCurrency(list, search) {
    var out = [];
    var re = new RegExp(search.searchText, 'i');
    for (var i = 0; i < list.length; i++) {
        if (search.criteria) {
            if (search.criteria(list[i])) {
                out.push(list[i]);
            }
        }
        else if ((!search.registrationCurrency || search.registrationCurrency === list[i].currency.currency) && (!search.searchText || re.test(list[i].name))) {
            out.push(list[i]);
        }
    }
    return out;
}
