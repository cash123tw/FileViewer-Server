import { urls } from '../../modal/urls.js'
import 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js'

export function getUserDetail(){
    return  $.ajax({
        url: urls.user.detail.url,
        type: urls.user.detail.method,
        async:false,
    })
}
