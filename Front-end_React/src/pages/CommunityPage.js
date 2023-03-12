import '../../node_modules/bootstrap/dist/css/bootstrap.css';
import '../common_static/common.css';
import '../common_static/search.css';

function CommunityPage(){
    return(
        <div class="d-flex flex-row">
            <div class="my-auto">
                <img alt="" src="https://ps.w.org/new-grid-gallery/assets/icon-256x256.png?rev=1957969" class="mx-1" />
            </div>
            <div class="my-auto" style={{ flexGrow: 1}}>
                <form class="form-inline" autocomplete="off">
                    <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" id="searchbar" />
                    <div class="data" id="search-box"></div>
                </form>
            </div>
            <div class="col-lg-4 col-md-6 col-sm-12 gallery-thumbnails">
                <a href="/"><img alt="" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpkaZlpNgMZQXu17ztcOyHIgsvtld0gtwD5A&usqp=CAU" class="img-thumbnail" /></a>
            </div>
        </div>
    );
}

export default CommunityPage;