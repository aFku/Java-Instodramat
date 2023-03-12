import '../../node_modules/bootstrap/dist/css/bootstrap.css';
import '../common_static/common.css';

function MainPage(){
    return( <div class="row justify-content-center">
    <div class="col-lg-10 px-0" id="photos_container">
        <div class="row  mb-3">
            <div class="card px-0">
                <div class="card-header">
                    <a href="/" style={{textDecoration: null, color: "black"}}>
                        <img src="https://d2bzsyjwknqwf6.cloudfront.net/?service=WMS&request=GetMap&layers=62212bea-f202-11e3-ac4e-22000b2f8216&styles=&format=image%2Fpng&transparent=true&version=1.1.1&unloadInvisibleTiles=false&tiled=yes&isVisible=true&worldCopyJump=false&maptiks_id=62212bea-f202-11e3-ac4e-22000b2f8216&tmp=null&width=256&height=256&srs=EPSG%3A3857&bbox=11679577.921974933,1296371.9997165895,11680800.914427496,1297594.9921691534" alt="" style={{height: "50px", borderRadius: "50px"}} />
                        <b><span class="author-link my-auto">aFku</span></b>
                    </a>
                </div>
                <div class="card-body">
                    <div style={{textAlign: "center"}}>
                        <a href="/"><img src="https://iust-projects.ir/assets/images/projects/dip/8/images/image2.jpg" alt="" style={{height: "500px"}} /></a>
                    </div>
                    <hr />
                    <div>
                        <b>Likes:</b> 5
                    </div>
                </div>
                <div class="card-footer" style={{textAlign: "center"}}>
                    <a href="/" class="go-to-photo-link">
                        Click here to see the photo!
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>);
}

export default MainPage;