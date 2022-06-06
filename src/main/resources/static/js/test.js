$(window).on('load', () => {
    $('#b1').on('click', click)
})

function click() {
    try {
        let btn = $(this);
        btn.preventDefault();
        log();
    }catch (e){
        console.log(e);
    }
    alert('finish')
    return false;
}

function log() {
    alert('xxx')
    console.log('xxx')
}

