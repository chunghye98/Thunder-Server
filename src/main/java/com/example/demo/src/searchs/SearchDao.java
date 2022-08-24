package com.example.demo.src.searchs;

import com.example.demo.src.searchs.model.GetBrands;
import com.example.demo.src.searchs.model.GetSearchesRes;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    public List<GetSearchesRes> getSearches(int userIdx) {
        String getSearchesQuery =
                "select u.userIdx, searchText\n" +
                        "from Users u\n" +
                        "join Searchs s on u.userIdx = s.userIdx\n" +
                        "where u.userIdx = ?\n" +
                        "order by s.updatedAt desc;";
        int getSearchesParams = userIdx;
        return this.jdbcTemplate.query(getSearchesQuery,
                (rs, rowNum) -> new GetSearchesRes(
                        rs.getInt("userIdx"),
                        rs.getString("searchText"),
                        getBrandNames(userIdx)),
                getSearchesParams);
    }

    private List<GetBrands> getBrandNames(int userIdx) {
        String getBrandNamesQuery =
                "select brandName,\n" +
                "       case when bf.status = 'enable' then 1 else 0 end as isFollowing\n" +
                "from Brands b\n" +
                "    left join(\n" +
                "        select brand_followIdx, followingBrandIdx, followerUserIdx, status\n" +
                "        from Brand_Follows\n" +
                "        where followerUserIdx = ? and status = 'enable'\n" +
                "    ) bf on bf.followingBrandIdx = b.brandIdx\n" +
                "limit 5;\n";
        int getBrandNamesParams = userIdx;
        return this.jdbcTemplate.query(getBrandNamesQuery,
                (rs, rowNum) -> new GetBrands(
                        rs.getString("brandName"),
                        rs.getInt("isFollowing")),
                getBrandNamesParams);
    }
}
