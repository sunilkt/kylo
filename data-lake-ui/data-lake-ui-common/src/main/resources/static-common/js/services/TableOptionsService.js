/**
 * Service to call out to Feed REST.
 *
 */
angular.module(COMMON_APP_MODULE_NAME).service('TableOptionsService', ['PaginationDataService', function (PaginationDataService) {
    this.sortOptions = {};
    var self = this;

  this.newSortOptions = function(key,labelValueMap, defaultValue, defaultDirection) {

        var sortOptions = Object.keys(labelValueMap).map(function(mapKey) {
        var value = labelValueMap[mapKey];
            var sortOption = {label:mapKey, value:value, direction:'', reverse:false, type:'sort'}
            if(defaultValue && value == defaultValue){
                sortOption['default'] =  defaultDirection || 'asc';
                sortOption['direction'] = defaultDirection || 'asc';
                sortOption['reverse'] = sortOption['direction'] == 'asc' ? false : true;
                sortOption['icon'] = sortOption['direction'] == 'asc' ? 'keyboard_arrow_up' : 'keyboard_arrow_down';
            }
            return sortOption;
        });
        self.sortOptions[key] = sortOptions;
      return sortOptions;
    }

    function clearOtherSorts(key,option){
        var sortOptions = self.sortOptions[key];
        if(sortOptions) {
            angular.forEach(sortOptions, function (sortOption, i) {
                if (sortOption !== option) {
                    sortOption.direction = '';
                    sortOption.icon = '';
                }
            });
        }
    }

    function getDefaultSortOption(key) {
        var sortOptions = self.sortOptions[key];
        var defaultSortOption = null;
        if(sortOptions) {
            angular.forEach(sortOptions,function(sortOption,i) {
                if(sortOption.default) {
                    defaultSortOption = sortOption;
                    return false;
                }
            });
        }
        return defaultSortOption;
    }

    this.saveSortOption = function(key,sortOption) {
        if(sortOption) {
            var val = sortOption.value;
            if(sortOption.reverse){
                val = '-'+val;
            }
            PaginationDataService.sort(key,val);
        }
    }


    this.toggleSort = function(key,option) {
        //single column sorting, clear sort if different
        clearOtherSorts(key,option)
        var returnedSortOption = option;
        if(option.direction == undefined || option.direction == '' || option.direction == 'desc') {
            option.direction = 'asc';
            option.icon = 'keyboard_arrow_up'
            option.reverse = false;
        }
        else if(option.direction == 'asc'){
            option.direction = 'desc';
            option.icon = 'keyboard_arrow_down'
            option.reverse = true;
        }
       // self.saveSortOption(key,returnedSortOption)
        return returnedSortOption;
    }
    this.toSortString = function(option) {
        if(option.direction == 'desc'){
            return "-"+option.value;
        }
        else {
            return option.value;
        }
    }

    this.setSortOption = function(key,val){
        var dir = 'asc'
        var icon = 'keyboard_arrow_up';
        var sortColumn = val;
        if(val.indexOf('-') == 0) {
            dir = 'desc';
            icon = 'keyboard_arrow_down';
            sortColumn = val.substring(1);
        }
        var sortOptions = self.sortOptions[key];
        angular.forEach(sortOptions,function(sortOption,i) {
           if(sortOption.value == sortColumn){
               sortOption.direction = dir
               sortOption.icon = icon;
               sortOption.reverse = dir =='desc' ? true : false;
           }
            else {
               sortOption.direction = '';
               sortOption.icon = '';
               sortOption.reverse = false;
           }

        });
    }

    this.getCurrentSort = function(key){

        var sortOptions = self.sortOptions[key];
        var returnedSortOption = null;
        if(sortOptions) {
            angular.forEach(sortOptions,function(sortOption,i) {
                if(sortOption.direction && sortOption.direction != '') {
                    returnedSortOption = sortOption;
                    return false;
                }
            });
            if(returnedSortOption == null) {
                returnedSortOption = getDefaultSortOption(key);
            }
        }
    return returnedSortOption;
}



}]);