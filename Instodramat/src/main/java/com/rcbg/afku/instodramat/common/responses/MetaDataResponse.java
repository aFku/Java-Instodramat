package com.rcbg.afku.instodramat.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataResponse extends RepresentationModel<MetaDataResponse> {
    protected MetaData metaData;
}
