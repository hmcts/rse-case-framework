package uk.gov.hmcts.ccd.v2.internal.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.hateoas.RepresentationModel;
import uk.gov.hmcts.ccd.domain.model.definition.Banner;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BannerViewResource extends RepresentationModel {

    private List<Banner> banners;

    public BannerViewResource(@NonNull List<Banner> listOfBanners) {
        copyProperties(listOfBanners);
    }

    private void copyProperties(List<Banner> listOfBanners) {
        this.banners = listOfBanners;
    }
}
