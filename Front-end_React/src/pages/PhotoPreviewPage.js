function PhotoPreviewPage(){
    return (
        <div>
        <div class="row">
        <div class="col-xl-12 d-flex justify-content-center">
            <img src="https://www.freeiconspng.com/thumbs/photography-icon-png/photography-icons-5.png" class="img-fluid image-border" width="50%" alt="" />
        </div>
        <div class="col-xl-12 d-flex justify-content-center">
            <div class="image-border util-bar" style={{width: "50%"}}>
               <div class="row d-flex justify-content-between like-bar" style={{margin: "2px 0"}}>
                   <div class="col-sm-8 d-flex align-items-center my-1">
                        <a href="/"><img src="https://taxa.krakow.pl/wp-content/uploads/2020/04/012-dashboard.png" class="img-fluid clickable" width="40vh" style={{margin: "auto"}} id="like-icon" alt=""/></a>
                        <a href="/"><img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRYaTuDzOsxUO7mAR8Jw7ot3013nC5Vf6zEPw&usqp=CAU" class="img-fluid clickable" width="40vh" style={{margin: "auto"}} id="like-icon"/></a>
                       <a class="like-stat clickable" onclick="get_list('like_list')">5 user(s) like it</a>
                   </div>
                   <div class="col-sm-2 d-flex align-items-center my-1 justify-content-sm-end dropdown-container">
                       <button class="clear-button dropdown-button" style={{marginRight: "2px"}}><img src="https://ps.w.org/shortpixel-image-optimiser/assets/icon-256x256.png?rev=1038819" class="img-fluid" width="7vh"/></button>
                       <div class="dropdown-content">
                           <a href="/">Delete photo</a>
                           <a href="/">Edit photo</a>
                       </div>
                   </div>
               </div>
                <div class="row" style={{margin: "2px 0"}}>
                   <div class="col-xl-12 description-content">
                       <b>
                           <a href="/" class="author-link">username</a>
                       </b>
                       <span class="image-description">
                           <a>description</a>
                       </span>
                       <br />
                       <span class="date-span">
                           publish date
                       </span>
                   </div>
               </div>
            </div>
        </div>
    </div>
    <div class="row mt-5">
        <div class="col-xl-12" id="comments-header">
            <h2>Comments count</h2>
        </div>
    </div>
    <div class="row">
        <form method="post" action="">
            <div class="col-xl-12 d-flex justify-content-center">
                Field
            </div>
            <div class="col-xl-12 d-flex justify-content-center">
                <input type="submit" value="Add comment" class="btn btn-secondary mx-3 my-3" style={{width: "50%"}} />
            </div>
        </form>
    </div>
    <div class="row mt-2">
            <div class="col-xl-12 d-flex justify-content-center">
                <div class="card mt-1" style={{width: "50%"}}>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-lg-6">
                                <a href="/" class="author-link"><h4>author</h4></a>
                            </div>
                            <div class="col-lg-6 ml-auto">
                                <div class="float-lg-end">
                                    <span class="date-span">
                                        <h5>
                                            comment publish date
                                        </h5>
                                    </span>
                                </div>
                            </div>
                            <div class="col-xl-12">
                                comment text content
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    </div>
    </div>
        );
}

export default PhotoPreviewPage;