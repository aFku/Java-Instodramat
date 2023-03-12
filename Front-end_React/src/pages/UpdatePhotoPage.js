function UpdatePhotoPage(){
    return (<div class="container h-100 d-flex justify-content-center" id="form-container">
        <div class="card my-auto" style={{width: "50%"}}>
            <form method="POST" id="formUpload" enctype="multipart/form-data">
                <div class="row mx-4">
                    <div class="col-lg-12 col-md-12 d-flex flex-column justify-content-center">
                    <h3 class="display-5">label_tag</h3>
                    Forma
                    </div>
                </div>
                <div class="row my-4">
                    <div class="col-xl-12" style={{textAlign: "center" }}>
                        <input type="submit" value="Edit photo" class="btn btn-secondary" id="save-model-button" />
                    </div>
                </div>
            </form>
        </div>
    </div>);
}

export default UpdatePhotoPage;