import '../node_modules/bootstrap/dist/css/bootstrap.css';
import './base.css';
import NavBar from './components/navbar/navbar';
import { Routes } from 'react-router-dom';
import { Route } from 'react-router-dom';

import AddPhotoPage from './pages/AddPhotoPage';
import CommunityPage from './pages/CommunityPage';
import MainPage from './pages/MainPage';
import ProfilePage from './pages/ProfilePage';
import SettingsPage from './pages/SettingsPage';
import UpdatePhotoPage from './pages/UpdatePhotoPage';
import PhotoPreviewPage from './pages/PhotoPreviewPage';

import Messages from './components/messages/messages';

function Base(){
    return(
        <div>
            <NavBar />
            <div class="container-fluid bg-light py-4" id="container-background">
                <div class="container mb-5">
                    <Messages />
                </div>
            </div>
            <div class="container" id="container-content">
                    <Routes>
                        <Route exact path="/" element={<MainPage />} />
                        <Route exact path="/profile" element={<ProfilePage />} />
                        <Route exact path="/community" element={<CommunityPage />} />
                        <Route exact path="/addPhoto" element={<AddPhotoPage />} />
                        <Route exact path="/updatePhoto" element={<UpdatePhotoPage />}/>
                        <Route exact path="/previewPhoto" element={<PhotoPreviewPage/>}/>
                    </Routes>
            </div>
        </div>
    );
}

export default Base;