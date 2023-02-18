package com.rcbg.afku.instodramat.authusers.responses;

import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.common.responses.MetaDataResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleProfileResponse extends MetaDataResponse {

    private ProfileDto data;
}
