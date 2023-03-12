import '../../../node_modules/bootstrap/dist/css/bootstrap.min.css';
import '../../common_static/common.css';

function NavBar(){
    return(<nav class="navbar navbar-background-grey navbar-expand-lg">
    <div class="container">
        <a class="navbar-brand" href="/">
            <img src="" alt="" class="d-inline-block align-top" width="51.2" height="38" />
            <span class="align-middle navbar-title">Instodramat</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"><img src="" alt="" /></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
            <li class="nav-item mx-2 navbar-item-flex-center">
                <a class="nav-link" href="/">
                    <img src=""  alt="" class="mx-1" />
                    <span class="align-middle"> Home </span>
                </a>
            </li>
            <li class="nav-item mx-2 navbar-item-flex-center">
                <a class="nav-link" href="/">
                    <img src=""  alt="" class="mx-1"/>
                    <span class="align-middle mx-1"> Add new photo </span>
                </a>
            </li>
            <li class="nav-item mx-2 navbar-item-flex-center">
                <a class="nav-link" href="/">
                    <img src=""  alt="" class="mx-1"/>
                    <span class="align-middle mx-1"> Community </span>
                </a>
            </li>
            <li class="nav-item mx-2 dropdown">
                <a class="nav-link dropdown-toggle navbar-item-flex-center" href="/" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <img src=""  alt="" class="mx-1"/>
                    <span class="align-middle mx-1"> Account </span>
                    <img src=""  alt="" class="d-inline-block align-top mx-1" width="51.2" height="42.8" style={{borderRadius: "40px"}}/>
                </a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li><a class="dropdown-item" href="/"><img  alt="" src="" class="mx-1"/>Profile</a></li>
                    <li><a class="dropdown-item" href="/"><img  alt="" src="" class="mx-1"/>Settings</a></li>
                    <li><hr class="dropdown-divider"/></li>
                    <li><a class="dropdown-item" href="/"><img  alt="" src="" class="mx-1"/>Log out</a></li>
                </ul>
            </li>
            <li class="nav-item mx-2 navbar-item-flex-center">
                <a class="nav-link" href="/">
                    <img src=""  alt="" class="mx-1"/>
                    <span class="align-middle mx-1"> Log in </span>
                </a>
            </li>
            <li class="nav-item mx-2 navbar-item-flex-center">
                <a class="nav-link" href="/  m
                ">
                    <img src=""  alt="" class="mx-1"/>
                    <span class="align-middle mx-1"> Sign up! </span>
                </a>
            </li>
        </ul>
        </div>
    
    </div>
    </nav>);
}

export default NavBar;